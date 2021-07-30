package br.com.mh.csv.conf;

import br.com.mh.csv.batch.CompraItemProcessor;
import br.com.mh.csv.batch.CompraItemReader;
import br.com.mh.csv.batch.CompraItemWriter;
import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.listener.CompraStepExecutionListener;
import br.com.mh.csv.util.FileReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("${file.path}")
    private Resource[] inputFilesArray;

    @Value("${file.name.key}")
    private String fileNameKey;

    @Value("${file.reader.key}")
    private String fileReaderKey;

    @Value("${file.column.line}")
    private Integer fileColumnLineNumber;

    private static final int THREAD_LIMIT = 5;

    @StepScope
    @Bean(name = "listener")
    public StepExecutionListener listener(@Value("#{stepExecutionContext[fileReader]}") FileReader fileReader) {
        return new CompraStepExecutionListener(fileReader);
    }

    @Bean(name = "partitioner")
    public Partitioner partitioner() {
        return new CompraPartitioner(inputFilesArray, fileNameKey, fileReaderKey, fileColumnLineNumber);
    }

    @Bean
    @StepScope
    public ItemReader<Compra> itemReader(
            @Value("#{stepExecutionContext[fileReader]}") FileReader fileReader,
            @Value("#{stepExecutionContext[fileName]}") String fileName) {

        return new CompraItemReader(fileReader, fileColumnLineNumber.longValue(), fileName);
    }

    @Bean
    @StepScope
    public ItemProcessor<Compra, CompraDomain> itemProcessor() {
        return new CompraItemProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<CompraDomain> itemWriter(@Value("#{stepExecutionContext[threadName]}")String threadName) {
        return new CompraItemWriter(threadName);
    }

    @Bean(name = "masterStep")
    public Step firstStepManager(@Qualifier("slaveStep") Step step, @Qualifier("partitioner") Partitioner partitioner) {
        return steps.get("masterStep")
                .partitioner("slaveStep", partitioner)
                .step(step)
                .gridSize(THREAD_LIMIT)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean(name = "slaveStep")
    protected Step slaveStep(ItemReader<Compra> reader,
                             ItemProcessor<Compra, CompraDomain> processor,
                             ItemWriter<CompraDomain> writer,
                             StepExecutionListener listener) {

        return steps.get("slaveStep")
                .<Compra, CompraDomain> chunk(5000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .listener(listener)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .throttleLimit(THREAD_LIMIT)
                .build();
    }

    @Bean(name = "compraBatchJob")
    public Job job(@Qualifier("masterStep") Step firstStepManager) {
        return jobs.get("compraBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStepManager)
                .build();
    }
}
