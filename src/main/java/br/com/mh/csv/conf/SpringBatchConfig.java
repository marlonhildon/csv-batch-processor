package br.com.mh.csv.conf;

import br.com.mh.csv.batch.CompraItemProcessor;
import br.com.mh.csv.batch.CompraItemWriter;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.domain.FileColumn;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("process/base_teste.txt")
    private Resource inputFile;

    private final int FIRST_LINE = 1;

    @Bean
    public ItemReader<CompraDomain> itemReader() {
        return new FlatFileItemReaderBuilder<CompraDomain>()
                .name("compraItemReader")
                .resource(inputFile)
                .linesToSkip(FIRST_LINE)
                .lineTokenizer(new FixedLengthTokenizer() {{
                    setNames(CompraDomain.getAllOrderedAttributesNamesArray());
                    setColumns(FileColumn.getAllOrderedColumnRanges());
                }})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{ setTargetType(CompraDomain.class); }})
                .build();
    }

    @Bean
    public ItemProcessor<CompraDomain, CompraDomain> itemProcessor() {
        return new CompraItemProcessor();
    }

    @Bean
    public ItemWriter<CompraDomain> itemWriter() {
        return new CompraItemWriter();
    }

    @Bean(name = "firstStep")
    protected Step firstStep(ItemReader<CompraDomain> reader,
                            ItemProcessor<CompraDomain, CompraDomain> processor,
                             ItemWriter<CompraDomain> writer) {

        return steps.get("firstStep")
                .<CompraDomain, CompraDomain> chunk(5000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(name = "compraBatchJob")
    public Job job(@Qualifier("firstStep") Step firstStep) {
        return jobs.get("compraBatchJob")
                .start(firstStep)
                .build();
    }
}
