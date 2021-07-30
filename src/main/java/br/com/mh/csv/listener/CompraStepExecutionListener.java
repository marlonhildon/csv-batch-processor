package br.com.mh.csv.listener;

import br.com.mh.csv.exception.CompraItemWritterException;
import br.com.mh.csv.util.FileReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@AllArgsConstructor
public class CompraStepExecutionListener implements StepExecutionListener {

    private final FileReader fileReader;
    private final String fileReadPath;
    private final String folderSucessPath;
    private final String folderErrorPath;
    private final String fileName;

    @Override
    public void  beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Fecha o LineNumberReader após o fim do Step para assegurar que nenhuma thread usará um LineNumberReader fechado.
        try {
            fileReader.closeLineNumberReader();
            this.moveFileToFolder(stepExecution.getExitStatus().getExitCode());
        } catch (IOException e) {
            log.error("Falha no afterStep: {}", e.getMessage());
            throw new CompraItemWritterException("Falha no afterStep: " + e.getMessage(), e);
        }

        return null;
    }

    private void moveFileToFolder(String exitStatus) throws IOException {
        switch(exitStatus) {
            case "COMPLETED":
                log.info("Movendo arquivo {} para a pasta de sucesso de processamento", fileName);
                Files.move(Paths.get(fileReadPath), Paths.get(folderSucessPath+fileName));
                break;
            case "FAILED":
                log.error("Movendo arquivo {} para a pasta de erro de processamento", fileName);
                Files.move(Paths.get(fileReadPath), Paths.get(folderErrorPath+fileName));
                break;
            default:
                log.warn("O arquivo não será movido");
        }
    }

}
