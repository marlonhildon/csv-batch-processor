package br.com.mh.csv.util;

import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.domain.FileColumn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties({"lineNumberReader", "lineMapper"})
public class FileReader implements Serializable {

    private transient LineNumberReader lineNumberReader;

    /**
     * Pula uma determinada quantidade de linhas lidas pelo LineNumberReader.
     * @param linesToSkip a quantidade de linhas a serem puladas
     * @param lineNumberReader o LineNumberReader usado
     * @throws IOException lançável pelo método readLine do LineNumberReader
     */
    public void skipLines(int linesToSkip, LineNumberReader lineNumberReader) throws IOException {
        for(int i=1; i<=linesToSkip; i++) {
            lineNumberReader.readLine();
        }
        log.info("Quantidade de linhas puladas na leitura: {}", linesToSkip);
    }

    /**
     * Fecha o LineNumberReader.
     * @throws IOException lançável pelo método close do LineNumberReader.
     */
    public void closeLineNumberReader() throws IOException {
        if(this.lineNumberReader != null) {
            lineNumberReader.close();
            log.info("LineNumberReader fechado com sucesso");
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
