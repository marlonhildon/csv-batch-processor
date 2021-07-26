package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.batch.item.file.transform.Range;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FileColumn {

    CPF("CPF", 1, 19),
    PRIVATE("PRIVATE", 20, 31),
    INCOMPLETO("INCOMPLETO", 32, 43),
    DATA_ULTIMA_COMPRA("DATA DA ÚLTIMA COMPRA", 44, 65),
    TICKET_MEDIO("TICKET MÉDIO", 66, 87),
    TICKET_ULTIMA_COMPRA("TICKET DA ÚLTIMA COMPRA", 88, 111),
    LOJA_MAIS_FREQUENTE("LOJA MAIS FREQUÊNTE", 112, 131),
    LOJA_ULTIMA_COMPRA("LOJA DA ÚLTIMA COMPRA", 132, 149);

    private final String columnName;
    private final Integer indexStart;
    private final Integer indexEnd;

    /**
     * Retorna todos os intervalos dos campos que equivalem à sua respectiva coluna.
     * Os intervalos sempre estarão na mesma ordem de declaração deste Enum.
     * @return Range[]
     */
    public static Range[] getAllOrderedColumnRanges() {
        Range[] rangeArray = Arrays
                .stream(FileColumn.values())
                .map(fileColumn -> new Range(fileColumn.getIndexStart(), fileColumn.getIndexEnd()))
                .toArray(Range[]::new);

        int ultimoElementoArray = rangeArray.length - 1;
        rangeArray[ultimoElementoArray] = new Range(LOJA_ULTIMA_COMPRA.getIndexStart());

        return rangeArray;
    }

}
