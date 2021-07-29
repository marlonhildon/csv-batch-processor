package br.com.mh.csv.batch;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.util.CompraMapper;
import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
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
    private Integer currentLineReaded;

    @Autowired
    private CompraMapper compraMapper;

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private LineMapper<CompraRaw> lineMapper;
    private String fileName;
    private boolean instantiatedByExecutionContext;

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

        try {
            if((line = this.bufferedReader.readLine()) != null) {
                ++currentLineReaded;
                compraRaw = this.lineMapper.mapLine(line, currentLineReaded);
            }
            return this.mapCompraRawToCompra(compraRaw);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao ler arquivo de entrada: " + exception.getMessage());
        } finally {
            if(line == null) {
                this.fileReader.closeBufferedReader();
            }
        }

    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (!this.instantiatedByExecutionContext) {
            this.fileReader = Objects.requireNonNull((FileReader) executionContext.get(fileReaderKey));
            this.bufferedReader = this.fileReader.getBufferedReader();
            this.fileName = executionContext.getString(fileNameKey);
            this.lineMapper = this.fileReader.getLineMapper();
            this.instantiatedByExecutionContext = true;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }

    private Compra mapCompraRawToCompra(CompraRaw compraRaw) {
        Compra compra = null;

        if(compraRaw != null) {
            compra = this.compraMapper.toCompra(compraRaw);
            compra.setLinhaArquivo(this.currentLineReaded.longValue());
            compra.setNomeArquivo(this.fileName);
        }

        return compra;
    }

}
