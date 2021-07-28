DO $body$
BEGIN
    RAISE NOTICE 'Criando função is_numeric';

    CREATE OR REPLACE FUNCTION is_numeric(text) RETURNS BOOLEAN AS $$
    DECLARE x NUMERIC;
    BEGIN
        x = $1::NUMERIC;
        RETURN TRUE;
    EXCEPTION WHEN others THEN
        RETURN FALSE;
    END;
    $$ LANGUAGE plpgsql IMMUTABLE;

    RAISE NOTICE 'Função criada com sucesso';
END;
$body$;