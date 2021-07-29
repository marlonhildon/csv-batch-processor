package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraDomain extends Compra {

    private Integer id;
    private Integer idErroCpf;
    private Integer idErroLojaMaisFrequente;
    private Integer idErroLojaUltimaCompra;
    private String nomeUsuario;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;

}
