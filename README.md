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
└───DBCOMPRA
    ├───Tabelas
    │       compras
    │       erros
    │
    ├───Funções
    │        is_numeric(text)
    │        validate_cnpj(varchar)
    │        validate_cpf(varchar)
    │
    └───Procedures
            insert_compra(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, bigint, varchar)
```

Todos os scripts de criação dos itens acima se encontram no diretório [docker/postgresql.](https://github.com/marlonhildon/csv-batch-processor/tree/master/docker/postgresql)

* A tabela DBCOMPRA.compras servem para armazenar cada linha do arquivo lido
* A tabela DBCOMPRA.erros possui ocorrências pré-inseridas. Definem qual o erro de validação que as colunas CPF, LOJA MAIS FREQUENTE e LOJA DA ÚLTIMA COMPRA eventualmente possuam
* Função is_numeric: verifica se um (text) passado é um número
* Função validate_cnpj: de acordo com o algoritmo de geração de CNPJ, valida se um CNPJ é válido
* Função validate_cpf: de acordo com o algoritmo de geração de CPF, valida se um CPF é válido
* Procedure insert_compra: responsável por validar, remover caracteres especiais e persistir uma linha lida do arquivo de entrada

O relacionamento entre as tabelas *compras* e *erros* é:
![Relacionamento entre compras e erros](https://drive.google.com/uc?export=view&id=1WzL2xFKOqz4nbI550wH5OjHz6bm0Bj5r)

*Cada CPF/CNPJ pode ter apenas um erro, mas o mesmo erro pode estar atrelado a diversas compras*, já que os erros possíveis de CPF/CNPJ são finitos:
* CPF/CNPJ não é numérico (ou seja: possui números e letras)
* CPF não possui 11 algarismos (auto-explicativo)
* CNPJ não possui 14 algarismos (auto-explicativo)
* CPF/CNPJ inválido de acordo com o algoritmo de validação
    * O algoritmo usado para validar o CPF se chama ["módulo 11"](https://pt.wikipedia.org/wiki/Cadastro_de_Pessoas_F%C3%ADsicas#D%C3%ADgitos_verificadores)
    * O algoritmo validador do CNPJ [é semelhante ao do CPF](https://pt.wikipedia.org/wiki/Cadastro_Nacional_da_Pessoa_Jur%C3%ADdica#Algoritmo_de_Valida%C3%A7%C3%A3o[carece_de_fontes?])

Após executar o docker-compose (veja a seção [Como executar](#como-executar)) a porta **5432** estará aberta para conexão com o banco de dados. Os dados para conexão são:
* Porta: 5432
* Host: localhost
* Database: TRANSACOES
* Usuário: csv-batch
* Senha: csv-batch

Exemplo de conexão usando o DBeaver:
![Conexão via DBeaver](https://drive.google.com/uc?export=view&id=18PYbwaUEtNMy9YLdPQd2g-maVgIMBieJ)

Após a conexão, recomenda-se usar uma query simples para verificar se as linhas foram persistidas.
Exemplo:
```SQL
SELECT * FROM DBCOMPRA.compras
```

## Como executar
0. Criar as pastas ERROR, PROCESS e PROCESSED um diretório antes da raiz deste repositório. O diretório CSV-BATCH-PROCESSOR, ERROR, PROCESS e PROCESSED devem permanecer lado a lado, conforme é mostrado pelo tópico [Arquitetura](#arquitetura).
1. Dado que o(s) arquivos(s) .txt a serem lidos estejam na pasta PROCESS
2. Dado que o Docker junto com o Docker Compose esteja instalado no SO de execução deste repositório (veja como instalar em: [Install Docker Compose](https://docs.docker.com/compose/install/)
3. Estando dentro da pasta raiz deste repositório, usar o seguinte comando para iniciar:
```text
docker-compose -f .\csv-batch-docker-compose.yml up -d --build
```
4. Para acompanhar o log de execução do batch, use o comando:
```text
docker logs batch -f
```
O log de execução mostra o tempo de finalização do step (processo de leitura, validação e persistência das linhas dos arquivos) e o tempo total de execução do Batch:
```text
[...]
2021-08-01 08:31:17.885  INFO 1 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [masterStep] executed in 33ms
2021-08-01 08:31:17.933  INFO 1 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=compraBatchJob]] completed with the following parameters: [{Dspring-boot.run.arguments=--threads.files=${THREADS_FILES} --threads.aux=${THREADS_AUX}, run.id=1}] and the following status: [COMPLETED] in 93ms
2021-08-01 08:31:17.935  INFO 1 --- [           main] br.com.mh.csv.Application                : O Batch foi encerrado por completo após 2.2301302 segundos de execução.
[...]
```
5. Se os arquivos foram processados com sucesso, serão movidos para a pasta PROCESSED; se não, para a pasta ERROR
6. Confira se a tabela DBCOMPRA.compras foi populada com as linhas dos arquivos, conforme descrito na seção [Banco de dados](#banco-de-dados)
7. Para encerrar a execução, use o comando:
```text
docker-compose -f .\csv-batch-docker-compose.yml down -v
```

## Threads
Este batch possui suporte à threads. A quantidade de threads a serem utilizadas podem ser customizadas alterando o [Dockerfile](https://github.com/marlonhildon/csv-batch-processor/blob/master/Dockerfile) deste projeto.
**Antes de executar o projeto docker-compose** (conforme explicado pela seção [Como executar](#como-executar) basta customizar as seguintes variáveis de ambiente:
* THREAD_FILES: variável de ambiente que define quantos arquivos serão processados ao mesmo tempo, em paralelo. **Mínimo 1**
* THREAD_AUX: variável de ambiente que define quantas threads auxiliares cada arquivo terá para acelerar o processamento. **Mínimo 0**

Consulte o [Dockerfile do projeto](https://github.com/marlonhildon/csv-batch-processor/blob/e18b7295ac04b0962a208cf4d886b1afe670ad82/Dockerfile#L5).
