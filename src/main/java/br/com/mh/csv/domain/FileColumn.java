package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.batch.item.file.transform.Range;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

@Getter
@AllArgsConstructor
public enum FileColumn {

    CPF("CPF"),
    PRIVATE("PRIVATE"),
    INCOMPLETO("INCOMPLETO"),
    DATA_ULTIMA_COMPRA("DATA DA ÚLTIMA COMPRA"),
    TICKET_MEDIO("TICKET MÉDIO"),
    TICKET_ULTIMA_COMPRA("TICKET DA ÚLTIMA COMPRA"),
    LOJA_MAIS_FREQUENTE("LOJA MAIS FREQUÊNTE"),
    LOJA_ULTIMA_COMPRA("LOJA DA ÚLTIMA COMPRA");

    private final String columnName;

    /**
     * Retorna todos os intervalos dos campos que equivalem à sua respectiva coluna.
     * Os intervalos sempre estarão na mesma ordem de declaração deste Enum.
     * @return Range[]
     */
    public static Range[] getAllOrderedColumnRanges(List<TreeSet<Integer>> columnIndexes) {
        Range[] rangeArray = columnIndexes.stream()
                .map(columnTreeSet -> new Range(columnTreeSet.first(), columnTreeSet.last()))
                .toArray(Range[]::new);

        int ultimoElementoArray = rangeArray.length - 1;
        rangeArray[ultimoElementoArray] = new Range(rangeArray[ultimoElementoArray].getMin());

        return rangeArray;
    }

}
