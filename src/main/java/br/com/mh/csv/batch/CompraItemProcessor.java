package br.com.mh.csv.batch;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.util.CompraMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class CompraItemProcessor implements ItemProcessor<Compra, CompraDomain>, ItemStream {

    @Autowired
    private CompraMapper compraMapper;

    @Value("${batch.user.name}")
    private String userName;

    @Override
    public CompraDomain process(Compra compraRaw) throws Exception {
        CompraDomain compraDomain = this.compraMapper.toCompraDomain(compraRaw);
        compraDomain.setNomeUsuario(userName);

        return compraDomain;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }

}
