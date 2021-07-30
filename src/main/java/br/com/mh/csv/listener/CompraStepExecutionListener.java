package br.com.mh.csv.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompraStepExecutionListener implements StepExecutionListener {

    @Value("${file.reader.key}")
    private String fileReaderKey;

    @Override
    public void  beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Fecha o BufferedReader após o fim do Step para assegurar que nenhuma thread usará um BufferedReader fechado.
//        try {
//            FileReader fileReader = ((FileReader) Objects.requireNonNull(stepExecution.getExecutionContext().get(fileReaderKey)));
//            fileReader.closeBufferedReader();
//        } catch (IOException e) {
//            log.error("Falha ao fechar BufferedReader: {}", e.getMessage());
//            throw new CompraItemWritterException("Falha ao fechar BufferedReader: " + e.getMessage(), e);
//        }
        return null;
    }



}
