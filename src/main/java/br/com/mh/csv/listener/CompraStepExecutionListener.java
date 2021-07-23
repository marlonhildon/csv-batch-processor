package br.com.mh.csv.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CompraStepExecutionListener implements StepExecutionListener {

    @Value("process/base_teste.txt")
    private Resource inputFile;

    @Value("${lines.skip}")
    private int linesToSkip;

    @Value("${is.lines.skipped}")
    private Boolean isLinesSkipped;

    private static final String bufferedReaderKey = "bufferedReader";
    private static final String allItemsReadedKey = "allItemsReaded";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            if (!stepExecution.getExecutionContext().containsKey(bufferedReaderKey)) {
                stepExecution.getExecutionContext().put(bufferedReaderKey, this.getBufferedReaderInstance());
                this.skipLines(linesToSkip, isLinesSkipped, (BufferedReader) stepExecution.getExecutionContext().get(bufferedReaderKey));
                this.isLinesSkipped = Boolean.TRUE;
            }
            if (!stepExecution.getExecutionContext().containsKey(allItemsReadedKey)) {
                stepExecution.getExecutionContext().put(allItemsReadedKey, Boolean.FALSE);
            }
        } catch(IOException e) {
            log.error("Falha ao instanciar BufferedReader: {}", e.getMessage());
        } finally {
            try {
                BufferedReader bufferedReader = (BufferedReader) stepExecution.getExecutionContext().get(bufferedReaderKey);
                Boolean allItemsReaded = (Boolean) stepExecution.getExecutionContext().get(allItemsReadedKey);
                this.closeBufferedReader(allItemsReaded, bufferedReader);
            } catch (IOException e) {
                log.error("Falha ao fechar BufferedReader: {}", e.getMessage());
            }
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    /**
     * Instancia o BufferedReader do CompraItemReader.
     * @throws IOException lançável pelo método create do DefaultBufferedReaderFactory.
     */
    private BufferedReader getBufferedReaderInstance() throws IOException {
        log.info("Instanciando o BufferedReader");
        return new DefaultBufferedReaderFactory().create(inputFile, StandardCharsets.UTF_8.name());
    }

    /**
     * Pula uma determinada quantidade de linhas lidas pelo BufferedReader.
     * @param linesToSkip a quantidade de linhas a serem puladas
     * @param isLinesSkipped informa se as linhas já foram puladas
     * @param bufferedReader o BufferedReader usado no {@link br.com.mh.csv.batch.CompraItemReader}
     * @throws IOException lançável pelo método readLine do BufferedReader
     */
    private void skipLines(int linesToSkip, Boolean isLinesSkipped, BufferedReader bufferedReader) throws IOException {
        if (isLinesSkipped) {
            for(int i=1; i<=linesToSkip; i++) {
                bufferedReader.readLine();
            }
            log.info("Quantidade de linhas puladas na leitura: {}", linesToSkip);
        }
    }

    /**
     * Fecha o BufferedReader do CompraItemReader.
     * @throws IOException lançável pelo método close do BufferedReader.
     */
    private void closeBufferedReader(Boolean allItemsReaded, BufferedReader bufferedReader) throws IOException {
        if(allItemsReaded && bufferedReader != null) {
            bufferedReader.close();
        }
    }

}
