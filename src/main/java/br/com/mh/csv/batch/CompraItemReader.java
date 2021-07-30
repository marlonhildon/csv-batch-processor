package br.com.mh.csv.batch;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.exception.CompraItemReaderException;
import br.com.mh.csv.util.CompraMapper;
import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CompraItemReader implements ItemStreamReader<Compra> {

    @Value("${file.reader.key}")
    private String fileReaderKey;

    @Value("${file.line.key}")
    private String fileLineKey;

    @Value("${file.name.key}")
    private String fileNameKey;

    @Value("${file.column.line}")
    private Long currentLineReaded;

    @Value("${file.column.line}")
    private Integer fileColumnLineNumber;

    @Autowired
    private CompraMapper compraMapper;

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private LineMapper<CompraRaw> lineMapper;
    private String fileName;
    private boolean instantiatedByExecutionContext;
    private String threadName;

    public CompraItemReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

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
    public Compra read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        CompraRaw compraRaw = null;
        String line = null;
        this.bufferedReader = this.fileReader.getBufferedReader();
        this.lineMapper = this.fileReader.getLineMapper();
        try {
            if((line = this.bufferedReader.readLine()) != null) {
                ++currentLineReaded;
                compraRaw = this.lineMapper.mapLine(line, currentLineReaded.intValue());
            }

            if(StringUtils.hasText(threadName)) {
                log.info("{} - ItemReader iniciado com o arquivo {}", threadName, fileName );
                threadName = null;
            }


            return this.mapCompraRawToCompra(compraRaw);
        } catch (Exception exception) {
            log.error("Erro ao ler arquivo de entrada: {}", exception.getMessage());
            throw new CompraItemReaderException("Erro ao ler arquivo de entrada: " + exception.getMessage(), exception);
        }

    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (!this.instantiatedByExecutionContext) {
            this.bufferedReader = this.fileReader.getBufferedReader();
            this.fileName = executionContext.getString(fileNameKey);
            this.lineMapper = this.fileReader.getLineMapper();
            this.threadName = executionContext.getString("threadName");
            this.skipLines();
            this.instantiatedByExecutionContext = true;
        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }

    private void skipLines() {
        try {
            this.fileReader.skipLines(this.fileColumnLineNumber, this.bufferedReader);
        } catch (IOException e) {
            log.error("Falha no uso do BufferedReader: {}", e.getMessage());
            throw new CompraItemReaderException("Falha no uso do BufferedReader: " + e.getMessage(), e);
        }
    }

    private Compra mapCompraRawToCompra(CompraRaw compraRaw) {
        Compra compra = null;

        if(compraRaw != null) {
            compra = this.compraMapper.toCompra(compraRaw);
            compra.setLinhaArquivo(this.currentLineReaded);
            compra.setNomeArquivo(this.fileName);
        }

        return compra;
    }

}
