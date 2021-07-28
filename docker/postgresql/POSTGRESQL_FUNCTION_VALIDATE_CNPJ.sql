create or replace function DBCOMPRA.validate_cnpj(cnpj varchar(14))
returns boolean as $$
declare
	dig_1 smallint := substring(cnpj from 1 for 1);
	dig_2 smallint := substring(cnpj from 2 for 1);
	dig_3 smallint := substring(cnpj from 3 for 1);
	dig_4 smallint := substring(cnpj from 4 for 1);
	dig_5 smallint := substring(cnpj from 5 for 1);
	dig_6 smallint := substring(cnpj from 6 for 1);
	dig_7 smallint := substring(cnpj from 7 for 1);
	dig_8 smallint := substring(cnpj from 8 for 1);
	dig_9 smallint := substring(cnpj from 9 for 1);
	dig_10 smallint := substring(cnpj from 10 for 1);
	dig_11 smallint := substring(cnpj from 11 for 1);
	dig_12 smallint := substring(cnpj from 12 for 1);
	dig_13 smallint := substring(cnpj from 13 for 1);
	dig_14 smallint := substring(cnpj from 14 for 1);
	dv_1_found smallint := 0;
	dv_2_found smallint := 0;
	mod_11 smallint := 0;
begin
	--Validação do primeiro dígito verificador
	mod_11 = mod(
		(dig_1*5 + dig_2*4 + dig_3*3 + dig_4*2 + dig_5*9 + dig_6*8 + dig_7*7 + dig_8*6 + dig_9*5 + dig_10*4 + dig_11*3 + dig_12*2),
		11
	);

	dv_1_found = case when (11-mod_11) = 10 then 0 else (11-mod_11) end;
	
	if dv_1_found <> dig_13 then 
		return false;
	end if;

	--Validação do segundo dígito verificador
	mod_11 = mod(
		(dig_1*6 + dig_2*5 + dig_3*4 + dig_4*3 + dig_5*2 + dig_6*9 + dig_7*8 + dig_8*7 + dig_9*6 + dig_10*5 + dig_11*4 + dig_12*3 + dv_1_found*2),
		11
	);

	dv_2_found = case when (11-mod_11) = 10 then 0 else (11-mod_11) end;
	
	if dv_2_found = dig_14 then 
		return true; 
	else 
		return false;
	end if;
end;
$$ language 'plpgsql';