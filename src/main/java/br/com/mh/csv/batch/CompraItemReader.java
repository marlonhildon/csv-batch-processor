package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.LineMapper;

import java.io.BufferedReader;
import java.util.Objects;

@Slf4j
public class CompraItemReader implements ItemStreamReader<CompraRaw> {

    private int currentLineReaded = 0;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private LineMapper<CompraRaw> lineMapper;
    private static final String fileReaderKey = "fileReader";

    /**
     * Processa os arquivos sem delimitadores específicos. <br>
     * Os delimitadores usados aqui são os intervalos dos caracteres das colunas do arquivo.
     * Os intervalos de caracteres entre as colunas estão presentes na classe {@link br.com.mh.csv.domain.FileColumn}
     * @return {@link br.com.mh.csv.domain.CompraRaw}
     * @throws Exception padrão da interface ItemReader
     * @throws UnexpectedInputException padrão da interface ItemReader
     * @throws ParseException padrão da interface ItemReader
     * @throws NonTransientResourceException padrão da interface ItemReader
     */
    @Override
    public CompraRaw read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        CompraRaw compraRaw = null;
        String line = null;

        try {
            if((line = this.bufferedReader.readLine()) != null) {
                ++currentLineReaded;
                compraRaw = this.lineMapper.mapLine(line, currentLineReaded);
            }
            return compraRaw;
        } catch (Exception exception) {
            log.error("Erro ao ler arquivo de entrada: {}", exception.getMessage());
            throw new RuntimeException("Erro ao ler arquivo de entrada: " + exception.getMessage());
        } finally {
            if(line == null) {
                this.fileReader.closeBufferedReader();
            }
        }

    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fileReader = Objects.requireNonNull((FileReader) executionContext.get(fileReaderKey));
        this.lineMapper = this.fileReader.getLineMapper();
        this.bufferedReader = this.fileReader.getBufferedReader();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }

}
