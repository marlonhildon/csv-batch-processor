CREATE SCHEMA DBCOMPRA;

CREATE TABLE DBCOMPRA.compras (
  id SERIAL PRIMARY KEY,
  cpf varchar(14) NOT NULL,
  flag_private bit,
  flag_incompleto bit,
  data_ultima_compra date,
  ticket_medio numeric(12,2),
  ticket_ultima_compra numeric(12,2),
  loja_mais_frequente varchar(18),
  loja_ultima_compra varchar(18),
  nome_arquivo varchar(255),
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