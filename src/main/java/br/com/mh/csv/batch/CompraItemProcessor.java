package br.com.mh.csv.batch;

import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.util.CompraMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class CompraItemProcessor implements ItemProcessor<Compra, CompraDomain> {

    @Autowired
    private CompraMapper compraMapper;

    @Value("${persistence.user-name}")
    private String username;

    @Override
    public CompraDomain process(Compra compra) {
        CompraDomain compraDomain = this.compraMapper.toCompraDomain(compra);
        compraDomain.setNomeUsuario(username);

        return compraDomain;
    }

}
