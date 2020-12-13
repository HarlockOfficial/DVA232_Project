DELIMITER $$
CREATE FUNCTION get_move (
    _playerCode VARCHAR(20), _gameCode VARCHAR(10)
) 
RETURNS VARCHAR(100) 
CHARSET utf8 COLLATE utf8_unicode_ci 
BEGIN
    declare _id int;
    declare _affected_rows int;
    declare _field varchar(17);
    declare _out varchar(100);
    select id, count(1), field into _id, _affected_rows, _field from current_matches where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);

    if _affected_rows>0 THEN
        if _gameCode != "ttt" THEN
            select move into _field from last_move where game_id=_id and player_code!=_playerCode;
            DELETE from last_move where game_id=_id and player_code!=_playerCode and move=_field;
        end if;
        return _field;
    end if;
    return "request parameter not valid";
END $$
DELIMITER ;