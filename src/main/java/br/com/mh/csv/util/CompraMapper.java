package br.com.mh.csv.util;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.domain.CompraRaw;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CompraMapper {

    /**
     * Converte automaticamente instâncias de {@link br.com.mh.csv.domain.Compra} para
     * {@link br.com.mh.csv.domain.CompraDomain}
     *
     * @param compra instância de {@link br.com.mh.csv.domain.Compra}
     * @return {@link br.com.mh.csv.domain.CompraDomain}
     */
    CompraDomain toCompraDomain(Compra compra);

    /**
     * Converte automaticamente instâncias de {@link br.com.mh.csv.domain.CompraRaw} para
     * {@link br.com.mh.csv.domain.Compra}
     *
     * @param compraRaw instância de {@link br.com.mh.csv.domain.CompraRaw}
     * @return {@link br.com.mh.csv.domain.Compra}
     */
    Compra toCompra(CompraRaw compraRaw);

}
