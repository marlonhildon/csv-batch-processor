package br.com.mh.csv.conf;

import br.com.mh.csv.batch.CompraItemProcessor;
import br.com.mh.csv.batch.CompraItemReader;
import br.com.mh.csv.batch.CompraItemWriter;
import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.listener.CompraStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

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
    public ItemReader<Compra> itemReader() {
        return new CompraItemReader();
    }

    @Bean
    public ItemProcessor<Compra, CompraDomain> itemProcessor() {
        return new CompraItemProcessor();
    }

    @Bean
    public ItemWriter<CompraDomain> itemWriter() {
        return new CompraItemWriter();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch_thread"); //Suporte à threads
    }

    @Bean(name = "firstStep")
    protected Step firstStep(ItemReader<Compra> reader,
                            ItemProcessor<Compra, CompraDomain> processor,
                             ItemWriter<CompraDomain> writer) {

        return steps.get("firstStep")
                .<Compra, CompraDomain> chunk(5000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(this.taskExecutor())
                .throttleLimit(10)
                .allowStartIfComplete(true)
                .listener(compraStepExecutionListener)
                .build();
    }

    @Bean(name = "compraBatchJob")
    public Job job(@Qualifier("firstStep") Step firstStep) {
        return jobs.get("compraBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStep)
                .build();
    }
}
