DELIMITER $$
DROP FUNCTION IF EXISTS `del_player`$$
CREATE FUNCTION `del_player` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) 
RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
    DECLARE _id INT DEFAULT 0; 
	DECLARE _affected_rows int DEFAULT 0 ;

    SELECT id, COUNT(1)  INTO _id, _affected_rows FROM multiplayer_queue WHERE game_code = _gameCode and player_code=_playerCode;
	IF _affected_rows>0 THEN
		DELETE FROM multiplayer_queue WHERE id=_id;
        return "ok";
	END IF;
	RETURN "player not in queue, cannot remove";
END$$
DELIMITER ;
