package br.com.mh.csv.conf;

import br.com.mh.csv.batch.CompraItemProcessor;
import br.com.mh.csv.batch.CompraItemReader;
import br.com.mh.csv.batch.CompraItemWriter;
import br.com.mh.csv.domain.CompraRaw;
import br.com.mh.csv.listener.CompraStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private CompraStepExecutionListener compraStepExecutionListener;

    @Bean
    public ItemReader<CompraRaw> itemReader() {
        return new CompraItemReader();
    }

    @Bean
    public ItemProcessor<CompraRaw, CompraRaw> itemProcessor() {
        return new CompraItemProcessor();
    }

    @Bean
    public ItemWriter<CompraRaw> itemWriter() {
        return new CompraItemWriter();
    }

    @Bean(name = "firstStep")
    protected Step firstStep(ItemReader<CompraRaw> reader,
                            ItemProcessor<CompraRaw, CompraRaw> processor,
                             ItemWriter<CompraRaw> writer) {

        return steps.get("firstStep")
                .<CompraRaw, CompraRaw> chunk(5000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(compraStepExecutionListener)
                .build();
    }

    @Bean(name = "compraBatchJob")
    public Job job(@Qualifier("firstStep") Step firstStep) {
        return jobs.get("compraBatchJob")
                .start(firstStep)
                .build();
    }
}
