package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraRaw;
import org.springframework.batch.item.ItemProcessor;

public class CompraItemProcessor implements ItemProcessor<CompraRaw, CompraRaw> {

    @Override
    public CompraRaw process(CompraRaw compraRaw) throws Exception {
        return compraRaw;
    }

}
