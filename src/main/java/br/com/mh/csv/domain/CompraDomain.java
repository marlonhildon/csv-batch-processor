package br.com.mh.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraDomain extends CompraRaw {

    private Integer id;
    private String nomeArquivo;
    private Integer linhaArquivo;
    private Integer idErroCpf;
    private Integer idErroLojaMaisFrequente;
    private Integer idErroLojaUltimaCompra;
    private String nomeUsuario;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;

}
