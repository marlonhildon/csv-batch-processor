package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraDomain;
import org.springframework.batch.item.ItemProcessor;

public class CompraItemProcessor implements ItemProcessor<CompraDomain, CompraDomain> {

    @Override
    public CompraDomain process(CompraDomain compraDomain) throws Exception {
        return compraDomain;
    }

}
