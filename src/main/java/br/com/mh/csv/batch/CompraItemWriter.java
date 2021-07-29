package br.com.mh.csv.batch;

import br.com.mh.csv.domain.CompraDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import java.util.List;

@Slf4j
public class CompraItemWriter implements ItemWriter<CompraDomain> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Value("${sql.procedure.insert-compra}")
    private String sqlProcedureInsertCompra;

    @Override
    public void write(List<? extends CompraDomain> list) throws Exception {
        log.info("Chunk de compras a serem gravadas na base: {}", list.size());
        try {
            jdbcTemplate.batchUpdate(sqlProcedureInsertCompra, SqlParameterSourceUtils.createBatch(list));
        } catch(Exception e) {
            log.error("Error in Batch Update: {}", e.getMessage());
            throw e;
        }
    }
}
