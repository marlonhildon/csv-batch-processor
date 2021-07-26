package br.com.mh.csv.util;

import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.domain.FileColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

@Getter
@Builder
@Slf4j
public class FileReader implements Serializable {

    private final transient BufferedReader bufferedReader;

    /**
     * Pula uma determinada quantidade de linhas lidas pelo BufferedReader.
     * @param linesToSkip a quantidade de linhas a serem puladas
     * @param bufferedReader o BufferedReader usado no {@link br.com.mh.csv.batch.CompraItemReader}
     * @throws IOException lançável pelo método readLine do BufferedReader
     */
    public void skipLines(int linesToSkip, BufferedReader bufferedReader) throws IOException {
        for(int i=1; i<=linesToSkip; i++) {
            bufferedReader.readLine();
        }
        log.info("Quantidade de linhas puladas na leitura: {}", linesToSkip);
    }

    /**
     * Fecha o BufferedReader do CompraItemReader.
     * @throws IOException lançável pelo método close do BufferedReader.
     */
    public void closeBufferedReader() throws IOException {
        if(this.bufferedReader != null) {
            bufferedReader.close();
            log.info("BufferedReader fechado com sucesso");
        }
    }

    /**
     * Define o LineMapper a ser usado pelo ItemReader. <br>
     * A divisão das colunas presentes no arquivo lido é realizada por intervalo de caracteres. <br>
     * @return LineMapper&lt;CompraRaw&gt; o LineMapper a ser usado para mapear de string para instância.
     */
    public LineMapper<CompraRaw> getLineMapper() {
        DefaultLineMapper<CompraRaw> defaultLineMapper = new DefaultLineMapper<>();
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();

        fixedLengthTokenizer.setNames(CompraRaw.getAllOrderedAttributesNamesArray());
        fixedLengthTokenizer.setColumns(FileColumn.getAllOrderedColumnRanges());

        defaultLineMapper.setLineTokenizer(fixedLengthTokenizer);
        defaultLineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{ setTargetType(CompraRaw.class); }});

        return defaultLineMapper;
    }

}
