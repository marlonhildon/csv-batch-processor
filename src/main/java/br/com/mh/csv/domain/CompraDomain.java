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
public class CompraDomain extends CompraRaw {

    private Integer id;
    private String nomeArquivo;
    private BigInteger linhaArquivo;
    private String nomeUsuario;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;

}
