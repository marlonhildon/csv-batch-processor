package br.com.mh.csv.batch;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.util.CompraMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CompraItemProcessor implements ItemProcessor<Compra, CompraDomain> {

    @Autowired
    private CompraMapper compraMapper;

    @Override
    public CompraDomain process(Compra compra) {
        return this.compraMapper.toCompraDomain(compra);
    }

}
