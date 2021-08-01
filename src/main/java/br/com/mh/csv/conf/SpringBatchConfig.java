package br.com.mh.csv.conf;

import br.com.mh.csv.batch.CompraItemProcessor;
import br.com.mh.csv.batch.CompraItemReader;
import br.com.mh.csv.batch.CompraItemWriter;
import br.com.mh.csv.domain.Compra;
import br.com.mh.csv.domain.CompraDomain;
import br.com.mh.csv.listener.CompraStepExecutionListener;
import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Slf4j
@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("${file.path.read}")
    private Resource[] inputFilesArray;

    @Value("${file.name.key}")
    private String fileNameKey;

    @Value("${file.reader.key}")
    private String fileReaderKey;

    @Value("${file.column.line}")
    private Integer fileColumnLineNumber;

    @Bean(name = "partitioner")
    public Partitioner partitioner() {
        return new CompraPartitioner(inputFilesArray, fileNameKey, fileReaderKey, fileColumnLineNumber);
    }

    @StepScope
    @Bean(name = "listener")
    public StepExecutionListener listener(
            @Value("#{stepExecutionContext[fileReader]}") FileReader fileReader,
            @Value("#{stepExecutionContext[filePath]}") String fileReadPath,
            @Value("${file.path.success}") String folderSuccessPath,
            @Value("${file.path.error}") String folderErrorPath,
            @Value("#{stepExecutionContext[fileName]}") String fileName) {
        return new CompraStepExecutionListener(fileReader, fileReadPath, folderSuccessPath, folderErrorPath, fileName);
    }

    @StepScope
    @Bean(name = "itemReader")
    public CompraItemReader itemReader(
            @Value("#{stepExecutionContext[fileReader]}") FileReader fileReader,
            @Value("#{stepExecutionContext[fileName]}") String fileName) {

        return new CompraItemReader(fileReader, fileColumnLineNumber.longValue(), fileName);
    }

    @StepScope
    @Bean(name = "itemProcessor")
    public CompraItemProcessor itemProcessor() {
        return new CompraItemProcessor();
    }

    @StepScope
    @Bean(name = "itemWriter")
    public CompraItemWriter getItemWriter(@Value("#{stepExecutionContext[threadName]}")String threadName) {
        return new CompraItemWriter(threadName);
    }

    @Bean(name = "masterStep")
    public Step manageMasterStep(
            @Qualifier("slaveStep") Step step,
            @Qualifier("partitioner") Partitioner partitioner,
            @Value("${threads.files}") int threadsFiles) {

        log.info("Threads for files: {}", threadsFiles);
        return steps.get("masterStep")
                .partitioner("slaveStep", partitioner)
                .step(step)
                .gridSize(threadsFiles > 0 ? threadsFiles : 1)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean(name = "slaveStep")
    protected Step manageSlaveStep(
            @Qualifier("itemReader") CompraItemReader reader,
            @Qualifier("itemProcessor") CompraItemProcessor processor,
            @Qualifier("itemWriter") CompraItemWriter writer,
            @Qualifier("listener") StepExecutionListener listener,
            @Value("${threads.aux}") int threadsAux) {

        log.info("Auxiliary threads: {}", threadsAux);
        return steps.get("slaveStep")
                .<Compra, CompraDomain> chunk(5000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .listener(listener)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .throttleLimit(threadsAux >= 0 ? threadsAux : 0)
                .build();
    }

    @Bean(name = "compraBatchJob")
    public Job manageJob(@Qualifier("masterStep") Step firstStepManager) {
        return jobs.get("compraBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStepManager)
                .build();
    }
}
