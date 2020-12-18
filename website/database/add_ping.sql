DELIMITER $$
DROP FUNCTION IF EXISTS `add_ping`$$
CREATE FUNCTION `add_ping` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) 
RETURNS VARCHAR(100) 
CHARSET utf8 COLLATE utf8_unicode_ci 
BEGIN
    declare _id int;
    declare _affected_rows int default 0;
    declare player1 varchar(20);
    declare player2 varchar(20);
    declare time_elapsed timestamp;
    select count(1) into _affected_rows from multiplayer_queue where player_code=_playerCode and game_code=_gameCode;
    if _affected_rows>0 then
        -- player is in multiplayer queue
        set _affected_rows = 0; 
        -- create or update the ping
        select id, count(1) into _id, _affected_rows from game_ping where game_code=_gameCode and player_code=_playerCode;
        if _affected_rows>0 then
            update game_ping set timestamp=NOW() where id=_id;
        else
            insert into game_ping(player_code, game_code, timestamp) values(_playerCode, _gameCode, NOW());
        end if;
        -- ok is always returned, just a check for the user that ping was successfull
        return "ok";
    end if;
    -- player was not in the multiplayer queue
    select player_code_1, player_code_2, count(1) into player1, player2, _affected_rows from current_matches where game_code=_gameCode and (player_code_1=_playerCode or player_code_2=_playerCode);
    if _affected_rows>0 then
        -- player is playing
        set _affected_rows = 0;
        -- can assume that timestamp exists, is created during multiplayer queue
        update game_ping set timestamp=NOW() where game_code=_gameCode and player_code=_playerCode;
        -- opponent ping is within 10 seconds? return "ok": return "error"
        if player1=_gameCode then
            set player1=player2;
        end if;
        select now()-timestamp into time_elapsed from game_ping where game_code=_gameCode and player_code=player1;
        if time_elapsed>10 then
            return "error";
        end if;
        return "ok";
    end if;
    return "Not allowed to ping, enter the playing queue before";
END$$
DELIMITER ;