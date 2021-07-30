package br.com.mh.csv.conf;

import br.com.mh.csv.exception.CompraItemReaderException;
import br.com.mh.csv.util.FileReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class CompraPartitioner implements Partitioner {

    private final Resource[] inputFilesArray;
    private final String fileNameKey;
    private final String fileReaderKey;
    private final int linesToSkip;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        HashMap<String, ExecutionContext> partitionHashMap = new HashMap<>();

        for(int i=0; i<inputFilesArray.length; i++) {
            ExecutionContext executionContext = new ExecutionContext();
            String fileName = inputFilesArray[i].getFilename();

            log.info("Iniciando Thread{}", i);
            log.info("Arquivo a ser processado: {}", fileName);
            executionContext.putString("threadName", "Thread"+i);
            executionContext.putString(fileNameKey, fileName);

            try {
                BufferedReader bufferedReader = this.getBufferedReaderInstance(inputFilesArray[i]);
                FileReader fileReader = new FileReader(bufferedReader);
                fileReader.skipLines(linesToSkip, fileReader.getBufferedReader());
                executionContext.put(fileReaderKey, new FileReader(bufferedReader));
            } catch(IOException e) {
                log.error("Falha no uso do BufferedReader: {}", e.getMessage());
                throw new CompraItemReaderException("Falha no uso do BufferedReader: {}" + e.getMessage(), e);
            }

            partitionHashMap.put("partition" + i, executionContext);
        }

        return partitionHashMap;
    }

    /**
     * Instancia o BufferedReader do CompraItemReader.
     * @throws IOException lançável pelo método create do DefaultBufferedReaderFactory.
     */
    private BufferedReader getBufferedReaderInstance(Resource resource) throws IOException {
        log.info("Instanciando o BufferedReader");
        return new DefaultBufferedReaderFactory().create(resource, StandardCharsets.UTF_8.name());
    }

}
