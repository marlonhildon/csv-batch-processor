package br.com.mh.csv.conf;

import br.com.mh.csv.exception.CompraItemReaderException;
import br.com.mh.csv.util.FileReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.LineNumberReader;
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
                LineNumberReader lineNumberReader = this.getLineNumberReaderInstance(inputFilesArray[i]);
                FileReader fileReader = new FileReader(lineNumberReader);
                fileReader.skipLines(linesToSkip, fileReader.getLineNumberReader());
                executionContext.put(fileReaderKey, new FileReader(lineNumberReader));
                executionContext.putString("filePath", inputFilesArray[i].getFile().getAbsolutePath());
            } catch(IOException e) {
                log.error("Falha no uso do LineNumberReader: {}", e.getMessage());
                throw new CompraItemReaderException("Falha no uso do LineNumberReader: {}" + e.getMessage(), e);
            }

            partitionHashMap.put("partition" + i, executionContext);
        }

        return partitionHashMap;
    }

    /**
     * Retorna uma instância de LineNumberReader de acordo com o arquivo a ser lido.
     * @throws IOException lançável pelo método create do DefaultBufferedReaderFactory.
     */
    private LineNumberReader getLineNumberReaderInstance(Resource resource) throws IOException {
        log.info("Instanciando o LineNumberReader");
        return new LineNumberReader(new DefaultBufferedReaderFactory().create(resource, StandardCharsets.UTF_8.name()));
    }

}
