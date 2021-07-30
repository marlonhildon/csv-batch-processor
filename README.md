# csv-batch-processor
Repositório Spring Batch responsável por ler arquivos txt no padrão csv, validar suas linhas e persisti-las num banco de dados PostgreSQL Docker Compose.
## Tecnologias usadas
- Maven 3.8.1
- Spring Boot 2.4.8
- JDK 11
## Arquitetura
A partir do ecossistema Spring Framework, foram usados o Spring Boot para iniciar a aplicação e o Spring Batch para processamento de dados em lotes.
O repositório consiste em um único módulo Maven com a seguinte estrutura:<br>

```text
CSV-BATCH-PROCESSOR
│   .gitignore
│   mvnw
│   mvnw.cmd
│   pom.xml
|   README.md
│
├───.mvn
│   └───wrapper
│           maven-wrapper.jar
│           maven-wrapper.properties
│           MavenWrapperDownloader.java
│
├───docker
│   │   postgresql-docker-compose.yml
│   │
│   └───postgresql
│           1-INIT_CREATE_TABLES.sql
│           2-INSERT_ERROS.sql
│           3-FUNCTION_IS_NUMERIC.sql
│           4-FUNCTION_VALIDATE_CNPJ.sql
│           5-FUNCTION_VALIDATE_CPF.sql
│           6-PROCEDURE_INSERT_COMPRA.sql
│
└───src
    └───main
        ├───java
        │   └───br
        │       └───com
        │           └───mh
        │               └───csv
        │                   │   Application.java
        │                   │
        │                   ├───batch
        │                   │       CompraItemProcessor.java
        │                   │       CompraItemReader.java
        │                   │       CompraItemWriter.java
        │                   │
        │                   ├───conf
        │                   │       CompraPartitioner.java
        │                   │       RepositoryConfig.java
        │                   │       SpringBatchConfig.java
        │                   │
        │                   ├───domain
        │                   │       Compra.java
        │                   │       CompraDomain.java
        │                   │       CompraRaw.java
        │                   │       ErroCompraDomain.java
        │                   │       FileColumn.java
        │                   │
        │                   ├───exception
        │                   │       CompraItemReaderException.java
        │                   │       CompraItemWritterException.java
        │                   │
        │                   ├───listener
        │                   │       CompraStepExecutionListener.java
        │                   │
        │                   └───util
        │                           CompraMapper.java
        │                           FileReader.java
        │
        └───resources
                application-sql.yml
                application.yml
ERROR
PROCESS
PROCESSED
```
## Estrutura do(s) arquivo(s) lido(s)

## Como o processamento dos arquivos é realizado

## Banco de dados

## Como executar
0. Criar as pastas ERROR, PROCESS e PROCESSED um diretório antes da raiz do clone deste repositório. O diretório CSV-BATCH-PROCESSOR, ERROR, PROCESS e PROCESSED devem permanecer lado a lado, conforme é mostrado pelo tópico [Arquitetura](#arquitetura).
1. Dado que o(s) arquivos(s) .txt a serem lidos estejam na pasta PROCESS
2. Dado que o Docker junto com o Docker Compose esteja instalado no SO de execução deste repositório (veja como instalar em: [Install Docker Compose](https://docs.docker.com/compose/install/)
3. Usar o comando ABCXYZ para executar o projeto
4. Se os arquivos foram processados com sucesso, serão movidos para a pasta PROCESSED; se não, para a pasta ERROR
5. Confira se a tabela DBCOMPRA.compras foi populada com as linhas dos arquivos, conforme descrito na seção [Banco de dados](#banco-de-dados)
