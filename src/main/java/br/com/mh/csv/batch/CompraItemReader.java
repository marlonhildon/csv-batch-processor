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

import java.io.LineNumberReader;

@Slf4j
public class CompraItemReader implements ItemReader<Compra> {

    @Autowired
    private CompraMapper compraMapper;

    private final LineNumberReader lineNumberReader;
    private final LineMapper<CompraRaw> lineMapper;
    private Long currentLineReaded;
    private final String fileName;

    public CompraItemReader(FileReader fileReader, Long currentLineReaded, String fileName) {
        this.lineNumberReader = fileReader.getLineNumberReader();
        this.lineMapper = fileReader.getLineMapper();
        this.currentLineReaded = currentLineReaded;
        this.fileName = fileName;
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
        String line;    
        CompraRaw compraRaw = null;
        Integer currentLineNumber = null;

        try {
            if((line = this.lineNumberReader.readLine()) != null) {
                currentLineNumber = lineNumberReader.getLineNumber();
                compraRaw = this.lineMapper.mapLine(line, currentLineNumber);

            }

            return this.mapCompraRawToCompra(compraRaw, currentLineNumber);
        } catch (Exception exception) {
            log.error("Erro ao ler arquivo de entrada: {}", exception.getMessage());
            throw new CompraItemReaderException("Erro ao ler arquivo de entrada: " + exception.getMessage(), exception);
        }

    }

    private Compra mapCompraRawToCompra(CompraRaw compraRaw, Integer currentLineReaded) {
        Compra compra = null;

        if(compraRaw != null) {
            compra = this.compraMapper.toCompra(compraRaw);
            compra.setLinhaArquivo(currentLineReaded.longValue());
            compra.setNomeArquivo(this.fileName);
        }

        return compra;
    }

}
