CREATE SCHEMA IF NOT EXISTS DBCOMPRA AUTHORIZATION "csv-batch";
DROP TABLE IF EXISTS DBCOMPRA.compras;
DROP TABLE IF EXISTS DBCOMPRA.erros;
DROP TABLE IF EXISTS DBCOMPRA.compras_erros;

CREATE TABLE DBCOMPRA.compras (
  "id" SERIAL PRIMARY KEY,
  "cpf" varchar(14),
  "flag_private" bit,
  "flag_incompleto" bit,
  "data_ultima_compra" date,
  "ticket_medio" numeric(12,2),
  "ticket_ultima_compra" numeric(12,2),
  "loja_mais_frequente" varchar(18),
  "loja_ultima_compra" varchar(18),
  "nome_arquivo" varchar(255),
  "linha_arquivo" bigint,
  "nome_usuario" varchar(255),
  "data_inclusao" timestamp,
  "data_alteracao" timestamp
);

CREATE TABLE DBCOMPRA.erros (
  "id" SERIAL PRIMARY KEY,
  "coluna_erro" varchar(50),
  "descricao_erro" varchar(255)
);

CREATE TABLE DBCOMPRA.compras_erros (
  "id" SERIAL PRIMARY KEY,
  "id_compra" int,
  "id_erro" int
);

ALTER TABLE DBCOMPRA.compras_erros ADD FOREIGN KEY ("id_compra") REFERENCES DBCOMPRA.compras ("id");

ALTER TABLE DBCOMPRA.compras_erros ADD FOREIGN KEY ("id_erro") REFERENCES DBCOMPRA.erros ("id");