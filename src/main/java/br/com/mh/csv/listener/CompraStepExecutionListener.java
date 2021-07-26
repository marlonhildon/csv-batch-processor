package br.com.mh.csv.listener;

import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
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

    @Value("${file.reader.key}")
    private String fileReaderKey;

    @Value("${file.name.key}")
    private String fileNameKey;

    @Value("${file.column.line}")
    private Integer fileColumnLineNumber;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        FileReader fileReader;
        BufferedReader bufferedReader;

        try {
            if (!stepExecution.getExecutionContext().containsKey(fileReaderKey)) {
                bufferedReader = this.getBufferedReaderInstance();
                fileReader = FileReader.builder().bufferedReader(bufferedReader).build();

                fileReader.skipLines(fileColumnLineNumber, bufferedReader);

                stepExecution.getExecutionContext().put(fileReaderKey, fileReader);
                stepExecution.getExecutionContext().putString(fileNameKey, this.inputFile.getFilename());
            }
        } catch(IOException e) {
            log.error("Falha no uso do BufferedReader: {}", e.getMessage());
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

}
