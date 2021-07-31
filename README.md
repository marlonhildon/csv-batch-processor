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
O arquivo possui extensão .txt e não possui separador de colunas. Logo, a estratégia utilizada foi atribuir um intervalo específico de caracteres para representar cada coluna.
As colunas e seus intervalos de caracteres são:
1. CPF (1-19) (Não-nulável)
2. PRIVATE (20-31)
3. INCOMPLETO (32-43)
4. DATA DA ÚLTIMA COMPRA (44-65)
5. TICKET MÉDIO (66-87)
6. TICKET DA ÚLTIMA COMPRA (88-111)
7. LOJA MAIS FREQUENTE (112-131)
8. LOJA DA ÚLTIMA COMPRA (132-149)

Exemplo:<br>
```text
CPF                PRIVATE     INCOMPLETO  DATA DA ÚLTIMA COMPRA TICKET MÉDIO          TICKET DA ÚLTIMA COMPRA LOJA MAIS FREQUÊNTE LOJA DA ÚLTIMA COMPRA
172.690.090-82     0           0           2010-12-02            400,60                400,60                  29.624.387/0001-39  29.624.387/0001-39
```

## Como o processamento dos arquivos é realizado
![Processo de processamento dos arquivos](https://drive.google.com/uc?export=view&id=1z7zXbndTPD_xFF2g1YrkHYdhIATDOVip)

## Banco de dados
O banco de dados escolhido é o PostgreSQL.
A estrutura de elementos do DB é a seguinte:

```text
TRANSACOES
│
├───DBCOMPRA
│   └───Tabelas
│           compras
│           erros
│
│   └───Funções
│           is_numeric(text)
│           validate_cnpj(varchar)
│           validate_cpf(varchar)
│
│   └───Procedures
            insert_compra(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, bigint, varchar)
```

Todos os scripts de criação dos itens acima se encontram no diretório [docker/postgresql.](https://github.com/marlonhildon/csv-batch-processor/tree/master/docker/postgresql)

* A tabela DBCOMPRA.compras servem para armazenar cada linha do arquivo lido
* A tabela DBCOMPRA.erros possui ocorrências pré-inseridas. Definem qual o erro de validação que as colunas CPF, LOJA MAIS FREQUENTE e LOJA DA ÚLTIMA COMPRA eventualmente possuam
* Função is_numeric: verifica se um (text) passado é um número
* Função validate_cnpj: de acordo com o algoritmo de geração de CNPJ, valida se um CNPJ é válido
* Função validate_cpf: de acordo com o algoritmo de geração de CPF, valida se um CPF é válido
* Procedure insert_compra: responsável por validar e persistir uma linha lida do arquivo de entrada 

## Como executar
0. Criar as pastas ERROR, PROCESS e PROCESSED um diretório antes da raiz do clone deste repositório. O diretório CSV-BATCH-PROCESSOR, ERROR, PROCESS e PROCESSED devem permanecer lado a lado, conforme é mostrado pelo tópico [Arquitetura](#arquitetura).
1. Dado que o(s) arquivos(s) .txt a serem lidos estejam na pasta PROCESS
2. Dado que o Docker junto com o Docker Compose esteja instalado no SO de execução deste repositório (veja como instalar em: [Install Docker Compose](https://docs.docker.com/compose/install/)
3. Usar o comando ABCXYZ para executar o projeto
4. Se os arquivos foram processados com sucesso, serão movidos para a pasta PROCESSED; se não, para a pasta ERROR
5. Confira se a tabela DBCOMPRA.compras foi populada com as linhas dos arquivos, conforme descrito na seção [Banco de dados](#banco-de-dados)
