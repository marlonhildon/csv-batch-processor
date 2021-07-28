DO $body$
BEGIN
    RAISE NOTICE 'Criando tabelas';

    CREATE SCHEMA DBCOMPRA;

    CREATE TABLE DBCOMPRA.compras (
      id SERIAL PRIMARY KEY,
      cpf varchar NOT NULL,
      flag_private varchar,
      flag_incompleto varchar,
      data_ultima_compra varchar,
      ticket_medio varchar,
      ticket_ultima_compra varchar,
      loja_mais_frequente varchar,
      loja_ultima_compra varchar,
      nome_arquivo varchar,
      linha_arquivo bigint,
      id_erro_cpf int,
      id_erro_loja_mais_frequente int,
      id_erro_loja_ultima_compra int,
      nome_usuario varchar(255),
      data_inclusao timestamp,
      data_alteracao timestamp
    );

    CREATE TABLE DBCOMPRA.erros (
      id SERIAL PRIMARY KEY,
      descricao_erro varchar
    );

    ALTER TABLE DBCOMPRA.compras ADD FOREIGN KEY (id_erro_cpf) REFERENCES DBCOMPRA.erros (id);

    ALTER TABLE DBCOMPRA.compras ADD FOREIGN KEY (id_erro_loja_mais_frequente) REFERENCES DBCOMPRA.erros (id);

    ALTER TABLE DBCOMPRA.compras ADD FOREIGN KEY (id_erro_loja_ultima_compra) REFERENCES DBCOMPRA.erros (id);

    RAISE NOTICE 'Operação realizada com sucesso';
END;
$body$;