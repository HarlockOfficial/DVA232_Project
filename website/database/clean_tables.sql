DELIMITER $$
--
-- Functions
--
DROP procedure IF EXISTS `clean_tables`$$
CREATE procedure `clean_tables` ()
BEGIN
    SET FOREIGN_KEY_CHECKS = 0; 
    truncate table game_ping;
    truncate table last_move;
    truncate table multiplayer_queue;
    truncate table current_matches;
    SET FOREIGN_KEY_CHECKS = 1; 
end $$
delimiter ;