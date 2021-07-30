package br.com.mh.csv.listener;

import br.com.mh.csv.exception.CompraItemWritterException;
import br.com.mh.csv.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.io.IOException;

@Slf4j
public class CompraStepExecutionListener implements StepExecutionListener {

    private final FileReader fileReader;

    public CompraStepExecutionListener(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public void  beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Fecha o LineNumberReader após o fim do Step para assegurar que nenhuma thread usará um LineNumberReader fechado.
        try {
            fileReader.closeLineNumberReader();
        } catch (IOException e) {
            log.error("Falha ao fechar LineNumberReader: {}", e.getMessage());
            throw new CompraItemWritterException("Falha ao fechar LineNumberReader: " + e.getMessage(), e);
        }
        return null;
    }

}
