package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraRaw;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class CompraItemWriter implements ItemWriter<CompraRaw> {

    @Override
    public void write(List<? extends CompraRaw> list) throws Exception {
        log.info("Chunk de compras a serem gravadas na base: {}", list.size());
    }
}
