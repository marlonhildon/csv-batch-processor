DO $body$
BEGIN
    RAISE NOTICE 'Inserindo os erros na tabela de erros';

    INSERT INTO DBCOMPRA.erros (descricao_erro)
    VALUES
        ('CPF/CNPJ não é numérico'),
        ('CPF não possui 11 algarismos'),
        ('CNPJ não possui 14 algarismos'),
        ('CPF/CNPJ inválido de acordo com o algoritmo de validação');

    RAISE NOTICE 'Erros inseridos com sucesso';
END;
$body$;