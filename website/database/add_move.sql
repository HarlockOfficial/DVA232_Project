DELIMITER $$
CREATE FUNCTION add_move (
    _playerCode VARCHAR(20), _gameCode VARCHAR(10), _position INT, _move varchar(10)
) 
RETURNS VARCHAR(100) 
CHARSET utf8 COLLATE utf8_unicode_ci 
BEGIN
    DECLARE _affected_rows int default 0;
    DECLARE _field varchar(17);
    declare _temporary int default 0;
    declare _game_id int;
    if _gameCode = "ttt" then
        select field, COUNT(1) into _field, _affected_rows from current_matches where game_code='ttt' and player_code=_playerCode;
        if _affected_rows > 0 then
            select insert(_field, _position, 1, _move) into _field;
            update current_matches set field=_field where game_code=_gameCode and player_code=_playerCode;
            return "ok";
        end if;
        return "request parameter not valid";
    elseif _gameCode = "dices" then
        set _affected_rows = 0;
        select id, count(1) into _game_id, _affected_rows from current_matches 
            where game_code='dices' and (player_code_1=_playerCode or player_code_2=_playerCode);
        if _affected_rows > 0 then
            set _affected_rows = 0;
            select count(1) into _affected_rows from last_move where game_id=_game_id and player_code=_player_code;
            if _affected_rows = 0 then
                set _move = 0;
                start_cicle: loop
                    SELECT RAND()*6+1 into _temporary;
                    set _move = _move + _temporary;
                    set _position = _position-1;
                    if _position>0 then
                        iterate start_cicle;
                    end if;
                    leave start_cicle;
                end loop start_cicle;
                insert into last_move(game_id, player_code, move) values (_game_id, _playerCode, _move);
                return _move;
            end if;
            return "cannot add more than one move";
        end if;
        return "request parameter not valid";
    elseif _gameCode = "rps" then
        set _affected_rows = 0;
        select id, count(1) into _game_id, _affected_rows from current_matches 
            where game_code='rps' and (player_code_1=_playerCode or player_code_2=_playerCode);
        if _affected_rows > 0 then
            set _affected_rows = 0;
            select count(1) into _affected_rows from last_move where game_id=_game_id and player_code=_player_code;
            if _affected_rows = 0 then
                insert into last_move(game_id, player_code, move) values (_game_id, _playerCode, _move);
                return "ok";
            end if;
            return "cannot add more than one move";
        end if;
        return "request parameter not valid";
    end if;
    return "request parameter not valid";
END $$
DELIMITER ;