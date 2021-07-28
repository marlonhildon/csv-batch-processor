create or replace function DBCOMPRA.validate_cpf(cpf varchar(11))
returns boolean as $$
declare
	dig_1 smallint := substring(cpf from 1 for 1);
	dig_2 smallint := substring(cpf from 2 for 1);
	dig_3 smallint := substring(cpf from 3 for 1);
	dig_4 smallint := substring(cpf from 4 for 1);
	dig_5 smallint := substring(cpf from 5 for 1);
	dig_6 smallint := substring(cpf from 6 for 1);
	dig_7 smallint := substring(cpf from 7 for 1);
	dig_8 smallint := substring(cpf from 8 for 1);
	dig_9 smallint := substring(cpf from 9 for 1);
	dig_10 smallint := substring(cpf from 10 for 1);
	dig_11 smallint := substring(cpf from 11 for 1);
	dv_1_found smallint := 0;
	dv_2_found smallint := 0;
	mod_11 smallint := 0;
begin
	--Validação do primeiro dígito verificador
	mod_11 = mod(
		(dig_1*10 + dig_2*9 + dig_3*8 + dig_4*7 + dig_5*6 + dig_6*5 + dig_7*4 + dig_8*3 + dig_9*2)*10,
		11
	);

	dv_1_found = case when mod_11 = 10 then 0 else mod_11 end;
	
	if dv_1_found <> dig_10 then 
		return false;
	end if;

	--Validação do segundo dígito verificador
	mod_11 = mod(
		(dig_1*11 + dig_2*10 + dig_3*9 + dig_4*8 + dig_5*7 + dig_6*6 + dig_7*5 + dig_8*4 + dig_9*3 + dv_1_found*2)*10,
		11
	);

	dv_2_found = case when mod_11 = 10 then 0 else mod_11 end;
	
	if dv_2_found = dig_11 then 
		return true; 
	else 
		return false;
	end if;
end;
$$ language 'plpgsql';