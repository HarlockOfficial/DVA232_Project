
DELIMITER $$
--
-- Functions
--
DROP FUNCTION IF EXISTS `add_move`$$
CREATE FUNCTION `add_move` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10), `_position` INT, `_move` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
    DECLARE _affected_rows int default 0;
    DECLARE _field varchar(17);
    declare _temporary int default 0;
	declare _dice_sum int default 0;
    declare _game_id int;
	declare _field_tmp varchar(100);
	declare player2 varchar(20);
	declare _condition int default 0;
	select 1 into _condition where _gameCode like "dices%";
    if _gameCode = "ttt" then
        select field, player_code_2, COUNT(1) into _field, player2, _affected_rows from current_matches where game_code='ttt' and player_code_1=_playerCode;
		if _affected_rows > 0 then
            select insert(_field, _position, 1, _move) into _field_tmp;
			set _field = _field_tmp;
			update current_matches set field=_field, player_code_1=player2, player_code_2=_playerCode where game_code=_gameCode and player_code_1=_playerCode;
			return "ok";
        end if;
        return "request parameter not valid";
    elseif _condition != 0 then
        set _affected_rows = 0;
        select id, count(1) into _game_id, _affected_rows from current_matches where game_code='dices' and (player_code_1=_playerCode or player_code_2=_playerCode);
        if _affected_rows > 0 then
            set _affected_rows = 0;
            select count(1) into _affected_rows from last_move where game_id=_game_id and player_code=_playerCode;
            if _affected_rows = 0 then
                start_cicle: loop
                    SELECT RAND()*6+1 into _temporary;
                    set _dice_sum = _dice_sum + _temporary;
                    set _position = _position-1;
                    if _position>0 then
                        iterate start_cicle;
                    end if;
                    leave start_cicle;
                end loop start_cicle;
                insert into last_move(game_id, player_code, move) values (_game_id, _playerCode, _dice_sum);
                return _dice_sum;
            end if;
            return "cannot add more than one move";
        end if;
        return "request parameter not valid";
    elseif _gameCode = "rps" then
        set _affected_rows = 0;
        select id, count(1) into _game_id, _affected_rows from current_matches where game_code='rps' and (player_code_1=_playerCode or player_code_2=_playerCode);
        if _affected_rows > 0 then
            set _affected_rows = 0;
            select count(1) into _affected_rows from last_move where game_id=_game_id and player_code=_playerCode;
            if _affected_rows = 0 then
                insert into last_move(game_id, player_code, move) values (_game_id, _playerCode, _move);
                return "ok";
            end if;
            return "cannot add more than one move";
        end if;
        return "request parameter not valid";
    elseif _gameCode = "blow" then
		set _affected_rows = 0;
		select id, field, count(1) into _game_id, _field, _affected_rows from current_matches where game_code='blow' and (player_code_1=_playerCode or player_code_2=_playerCode);
		if _affected_rows>0 then
            select count(1) into _affected_rows from last_move where game_id=_game_id and player_code=_playerCode;
			if _affected_rows <= 0 then
				select `move`, count(1) into _field_tmp, _affected_rows from last_move where game_id=_game_id and player_code!=_playerCode; 
				if _affected_rows > 0 then
					delete from last_move where game_id=_game_id and player_code!=_playerCode; 
					select convert(_field, signed) into _temporary;
					if _temporary<=0 or _temporary>=200 then
						return "match is already over";
					end if;
					select convert(_field_tmp, signed) into _dice_sum;
					update current_matches set field=_temporary+((_position-_dice_sum)/10) where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);
					select field into _field from current_matches where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);
					return _field;
				end if;
				insert into last_move(game_id, player_code, `move`) values (_game_id, _playerCode, _position);
				return "waiting for opponent move";
			end if;
			return "cannot add more than one move";
		end if;
		return "request parameter not valid";
	end if;
    return "request parameter not valid";
END$$

DROP FUNCTION IF EXISTS `get_move`$$
CREATE FUNCTION `get_move` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
    declare _id int;
    declare _affected_rows int;
    declare _field varchar(17);
    select id, count(1), field into _id, _affected_rows, _field from current_matches where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);

    if _affected_rows>0 THEN
        if _gameCode != "ttt" and _gameCode != "blow" THEN
            select move into _field from last_move where game_id=_id and player_code!=_playerCode;
            DELETE from last_move where game_id=_id and player_code!=_playerCode and move=_field;
        end if;
		return _field;
    end if;
    return "request parameter not valid";
END$$

DROP FUNCTION IF EXISTS `get_player`$$
CREATE FUNCTION `get_player` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
	DECLARE _id INT ;
	DECLARE _affected_rows BOOLEAN ;
	DECLARE _player_code varchar(20);
	DECLARE _field varchar(17);
	DECLARE _out varchar(70);
    select id, IF(COUNT(1)>0,TRUE,FALSE) INTO _id, _affected_rows FROM multiplayer_queue where player_code=_playerCode and game_code=_gameCode ;
	IF _affected_rows THEN
		RETURN "in_queue";
	ELSE
		select player_code_1, field, IF(COUNT(1)>0,TRUE,FALSE) INTO _player_code, _field, _affected_rows FROM current_matches where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);
		IF _affected_rows THEN
			IF _field IS NOT NULL THEN
				SET _out = CONCAT("{\"starting_player\":\"", _player_code, "\",\"field\":\"", _field, "\"}");
			ELSE
				SET _out = CONCAT("{\"starting_player\":\"", _player_code, "\",\"field\":\"NULL\"}");
			END IF;
			RETURN _out;
		ELSE
			return "request parameter not valid";
		END IF;
	END IF;
	return "impossible result";
END$$

DROP FUNCTION IF EXISTS `new_player`$$
CREATE FUNCTION `new_player` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
    DECLARE _id INT DEFAULT 0; 
	DECLARE _player_code VARCHAR(20) ; 
	DECLARE _affected_rows BOOLEAN DEFAULT FALSE ;
    DECLARE _player1 VARCHAR(20) ; 
	DECLARE _player2 VARCHAR(20) ; 
	DECLARE _field VARCHAR(17) ; 
	DECLARE _player_selector INT ;
    DECLARE _out VARCHAR(70);

    SELECT id, player_code,IF(COUNT(1) > 0, TRUE, FALSE)  INTO _id, _player_code, _affected_rows FROM multiplayer_queue WHERE game_code = _gameCode ORDER BY RAND() LIMIT 1 ;
	IF _affected_rows THEN
		DELETE FROM multiplayer_queue WHERE id=_id ; 
		SET _player_selector = FLOOR(RAND() * 10) % 2; 
		IF _player_selector = 0 THEN 
			SET _player1 = _playerCode ; 
			SET _player2 = _player_code ; 
		ELSE 
			SET _player1 = _player_code ; 
			SET _player2 = _playerCode ;
		END IF ; 
		IF _gameCode = "ttt" THEN 
			SET _field = "-,-,-,-,-,-,-,-,-" ; 
		
		ELSEIF _gameCode = "blow" then
			set _field = "100";
		else 
			SET _field = NULL ;
		END IF ;
		INSERT INTO current_matches(
				player_code_1,
				player_code_2,
				game_code,
				field
			)
			VALUES(
				_player1,
				_player2,
				_gameCode,
				_field
			) ; 
		IF _field IS NOT NULL THEN
			SET _OUT = CONCAT("{\"starting_player\":\"", _player1, "\",\"field\":\"", _field, "\"}");
		ELSE
			SET _OUT = CONCAT("{\"starting_player\":\"", _player1, "\",\"field\":\"NULL\"}");
		END IF;
        RETURN _out;
	ELSE
		INSERT INTO multiplayer_queue(player_code, game_code) VALUES(_playerCode, _gameCode) ; 
		RETURN "in_queue";
	END IF ;
END$$

DELIMITER ;
