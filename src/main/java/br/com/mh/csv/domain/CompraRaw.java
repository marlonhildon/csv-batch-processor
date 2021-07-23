package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraRaw {

    private String cpf;
    private String flagPrivate;
    private String flagIncompleto;
    private String dataUltimaCompra;
    private String ticketMedio;
    private String ticketUltimaCompra;
    private String lojaMaisFrequente;
    private String lojaUltimaCompra;

    /**
     * Retorna todos os nomes dos atributos desta classe, na mesma ordem em que são declarados.
     * @return String[]
     */
    public static String[] getAllOrderedAttributesNamesArray() {
        return Arrays
                .stream(CompraRaw.class.getDeclaredFields())
                .map(Field::getName)
                .toArray(String[]::new);
    }

}
