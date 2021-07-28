DO $body$
BEGIN
    RAISE NOTICE 'Criando procedure insert_compra';

    CREATE PROCEDURE insert_compra(
        cpf varchar,
        flag_private varchar,
        flag_incompleto varchar,
        data_ultima_compra varchar,
        ticket_medio varchar,
        ticket_ultima_compra varchar,
        loja_mais_frequente varchar,
        loja_ultima_compra varchar,
        nome_arquivo varchar(255),
        linha_arquivo bigint,
        nome_usuario varchar(255)
    )
    LANGUAGE 'plpgsql' AS $$
    DECLARE
        --Eliminando caracteres especiais
        cpf_sanitized varchar := regexp_replace(cpf,  '[^\w]+', '', 'g');
        flag_private_sanitized varchar := regexp_replace(flag_private,  '[^\w]+', '', 'g');
        flag_incompleto_sanitized varchar := regexp_replace(flag_incompleto,  '[^\w]+', '', 'g');
        data_ultima_compra_sanitized varchar := regexp_replace(data_ultima_compra,  '[^\w]+', '', 'g');
        ticket_medio_sanitized varchar := regexp_replace(ticket_medio,  '[^\w]+', '', 'g');
        ticket_ultima_compra_sanitized varchar := regexp_replace(ticket_ultima_compra,  '[^\w]+', '', 'g');
        loja_mais_frequente_sanitized varchar := regexp_replace(loja_mais_frequente,  '[^\w]+', '', 'g');
        loja_ultima_compra_sanitized varchar := regexp_replace(loja_ultima_compra,  '[^\w]+', '', 'g');

        --Validando se CPF e CNPJ são numéricos
        is_cpf_numeric boolean := is_numeric(cpf_sanitized);
        is_loja_mais_frequente_numeric boolean := is_numeric(loja_mais_frequente_sanitized);
        is_loja_ultima_compra_numeric boolean := is_numeric(loja_ultima_compra_sanitized);

        --Validando se CPF e CNPJ possuem comprimento válidos
        is_cpf_valid_length boolean := case when length(cpf_sanitized) = 11 then true else false end;
        is_loja_mais_frequente_valid_length boolean := case when length(loja_mais_frequente_sanitized) = 14 then true else false end;
        is_loja_ultima_compra_valid_length boolean := case when length(loja_ultima_compra_sanitized) = 14 then true else false end;

        --Validando se CPF e CNPJ são válidos diante o algoritmo de geração de CPF/CNPJ
        is_cpf_valid boolean := false;
        is_loja_mais_frequente_valid boolean := false;
        is_loja_ultima_compra_valid boolean := false;

        cpf_error_code int := null;
        loja_mais_frequente_error_code int := null;
        loja_ultima_compra_error_code int := null;

    BEGIN

        --------------------------------------------------------------------------------------------
        --------------------------------------------------------------------------------------------
        if NOT is_cpf_numeric then
            cpf_error_code = 1;
        elsif NOT is_cpf_valid_length then
            cpf_error_code = 2;
        else
            is_cpf_valid = validate_cpf(case when is_cpf_numeric then cpf_sanitized else null end);
        end if;

        if NOT is_loja_mais_frequente_numeric then
            loja_mais_frequente_error_code = 1;
        elsif NOT is_loja_mais_frequente_valid_length then
            loja_mais_frequente_error_code = 3;
        else
            is_loja_mais_frequente_valid = validate_cnpj(case when is_loja_mais_frequente_numeric then loja_mais_frequente_sanitized else null end);
        end if;

        if NOT is_loja_ultima_compra_numeric then
            loja_ultima_compra_error_code = 1;
        elsif NOT is_loja_ultima_compra_valid_length then
            loja_ultima_compra_error_code = 3;
        else
            is_loja_ultima_compra_valid = validate_cnpj(case when is_loja_ultima_compra_numeric then loja_ultima_compra_sanitized else null end);
        end if;
        --------------------------------------------------------------------------------------------
        --------------------------------------------------------------------------------------------
        if cpf_error_code is null and NOT is_cpf_valid then
            cpf_error_code = 4;
        end if;

        if loja_mais_frequente_error_code is null and NOT is_loja_mais_frequente_valid then
            loja_mais_frequente_error_code = 4;
        end if;

        if loja_ultima_compra_error_code is null and NOT is_loja_ultima_compra_valid then
            loja_ultima_compra_error_code = 4;
        end if;

        --Persistência
        INSERT INTO dbcompra.compras
        (
            cpf, flag_private, flag_incompleto, data_ultima_compra, ticket_medio, ticket_ultima_compra,
            loja_mais_frequente, loja_ultima_compra, nome_arquivo, linha_arquivo, id_erro_cpf, id_erro_loja_mais_frequente,
            id_erro_loja_ultima_compra, nome_usuario, data_inclusao, data_alteracao
        )
        VALUES
        (
            cpf_sanitized, flag_private_sanitized, flag_incompleto_sanitized, data_ultima_compra_sanitized, ticket_medio_sanitized, ticket_ultima_compra_sanitized,
            loja_mais_frequente_sanitized, loja_ultima_compra_sanitized, nome_arquivo, linha_arquivo, cpf_error_code, loja_mais_frequente_error_code,
            loja_ultima_compra_error_code, nome_usuario, CURRENT_TIMESTAMP, NULL
        );
    END;
    $$;

    RAISE NOTICE 'Procedure criada com sucesso';
END;
$body$;