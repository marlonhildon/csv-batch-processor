package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErroCompraDomain {

    private Integer cdErroProcessamento;
    private Character flErroCpf;
    private Character flErroLoja;

}
