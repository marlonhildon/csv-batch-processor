package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.domain.FileColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;

import java.io.BufferedReader;

@Slf4j
@Getter
@Setter
public class CompraItemReader implements ItemReader<CompraRaw>, ItemStream {

//    @Value("process/base_teste.txt")
//    private Resource inputFile;
    private ExecutionContext executionContext;
    private int currentLine = 0;
    private LineMapper<CompraRaw> lineMapper = this.getLineMapper();
    private transient BufferedReader bufferedReader;
    private static final String bufferedReaderKey = "bufferedReader";
    private static final String allItemsReadedKey = "allItemsReaded";

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

//        //TODO: substituir por ItemReaderListener, usar o método beforeRead() para instanciar o BufferedReader e pular linhas apenas uma vez
//        if (bufferedReader == null) {
//            bufferedReader = new DefaultBufferedReaderFactory().create(inputFile, StandardCharsets.UTF_8.name());
//        }

//        this.skipLines(1);
//        this.isLinesSkipped = Boolean.TRUE;

        try {
            if((line = bufferedReader.readLine()) != null) {
                currentLine++;
                compraRaw = lineMapper.mapLine(line, currentLine);
            }

            return compraRaw;
        } catch (Exception exception) {
            log.error("Erro ao ler arquivo de entrada: {}", exception.getMessage());
            throw new RuntimeException("Erro ao ler arquivo de entrada: " + exception.getMessage());
        } finally {
            if(line == null) {
                executionContext.put(allItemsReadedKey, Boolean.TRUE);
            }
        }

    }

    /**
     * Define o LineMapper a ser usado pelo ItemReader. <br>
     * A divisão das colunas presentes no arquivo lido é realizada por intervalo de caracteres. <br>
     * @return LineMapper&lt;CompraRaw&gt; o LineMapper a ser usado para mapear de string para instância.
     */
    private LineMapper<CompraRaw> getLineMapper() {
        DefaultLineMapper<CompraRaw> defaultLineMapper = new DefaultLineMapper<>();
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();

        fixedLengthTokenizer.setNames(CompraRaw.getAllOrderedAttributesNamesArray());
        fixedLengthTokenizer.setColumns(FileColumn.getAllOrderedColumnRanges());

        defaultLineMapper.setLineTokenizer(fixedLengthTokenizer);
        defaultLineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{ setTargetType(CompraRaw.class); }});

        return defaultLineMapper;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.bufferedReader = (BufferedReader) this.executionContext.get(bufferedReaderKey);
    }

    @Override
    public void close() throws ItemStreamException {

    }

//    /**
//     * Pula uma determinada quantidade de linhas lidas pelo BufferedReader.
//     * @param linesToSkip a quantidade de linhas a serem puladas
//     * @throws IOException lançável pelo método readLine do BufferedReader
//     */
//    private void skipLines(int linesToSkip) throws IOException {
//        if (!this.isLinesSkipped) {
//            for(int i=1; i<=linesToSkip; i++) {
//                this.bufferedReader.readLine();
//            }
//        }
//    }

}
