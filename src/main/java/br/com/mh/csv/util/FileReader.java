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
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Getter
@Builder
@Slf4j
public class FileReader implements Serializable {

    private final transient BufferedReader bufferedReader;

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
    public LineMapper<CompraRaw> getLineMapper(List<TreeSet<Integer>> columnIndexes) {
        DefaultLineMapper<CompraRaw> defaultLineMapper = new DefaultLineMapper<>();
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();

        fixedLengthTokenizer.setNames(CompraRaw.getAllOrderedAttributesNamesArray());
        fixedLengthTokenizer.setColumns(FileColumn.getAllOrderedColumnRanges(columnIndexes));

        defaultLineMapper.setLineTokenizer(fixedLengthTokenizer);
        defaultLineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{ setTargetType(CompraRaw.class); }});

        return defaultLineMapper;
    }

    public LinkedHashMap<FileColumn, TreeSet<Integer>> getFileColumnsIndexes(String columnsLine) {
        LinkedHashMap<FileColumn, TreeSet<Integer>> fileColumnHashMap = new LinkedHashMap<>();

        if(StringUtils.hasText(columnsLine)) {
            for(FileColumn fileColumn : FileColumn.values()) {
                TreeSet<Integer> indexesTreeSet = new TreeSet<>();
                int columnStartIndex = columnsLine.indexOf(fileColumn.getColumnName());
                int columnEndIndex = columnStartIndex + fileColumn.getColumnName().length();
                indexesTreeSet.add(++columnStartIndex); // Incremento para corrigir índice zero-based
                indexesTreeSet.add(columnEndIndex);

                fileColumnHashMap.put(fileColumn, indexesTreeSet);
            }
        }

        return this.getSortedLinkedHashMap(fileColumnHashMap);
    }

    private LinkedHashMap<FileColumn, TreeSet<Integer>> getSortedLinkedHashMap(LinkedHashMap<FileColumn, TreeSet<Integer>> linkedHashMap) {
        LinkedHashMap<FileColumn, TreeSet<Integer>> linkedHashMapSorted = new LinkedHashMap<>();
        List<TreeSet<Integer>> indexList = new ArrayList<>(linkedHashMap.values());
        indexList.sort(Comparator.comparing(TreeSet::first));

        for(TreeSet<Integer> indexInterval : indexList) {
            for(FileColumn column : linkedHashMap.keySet()) {
                if(linkedHashMap.get(column).equals(indexInterval)) {
                    linkedHashMapSorted.put(column, indexInterval);
                }
            }
        }

        return linkedHashMapSorted;
    }

}
