-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 11, 2020 at 12:24 PM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.3.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `id15598586_dva232_project_group_7`
--
DROP DATABASE IF EXISTS `id15598586_dva232_project_group_7`;
CREATE DATABASE IF NOT EXISTS `id15598586_dva232_project_group_7` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `id15598586_dva232_project_group_7`;

DELIMITER $$
--
-- Functions
--
DROP FUNCTION IF EXISTS `get_player`$$
CREATE DEFINER=`id15598586_root`@`%` FUNCTION `get_player` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
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
				SET _out = CONCAT("{'starting_player':", _player1, ",'field':", _field, "}");
			ELSE
				SET _out = CONCAT("{'starting_player':", _player1, ",'field':'NULL'}");
			END IF;
			RETURN _out;
		ELSE
			return "request parameter not valid";
		END IF;
	END IF;
END$$

DROP FUNCTION IF EXISTS `new_player`$$
CREATE DEFINER=`id15598586_root`@`%` FUNCTION `new_player` (`_playerCode` VARCHAR(20), `_gameCode` VARCHAR(10)) RETURNS VARCHAR(100) CHARSET utf8 COLLATE utf8_unicode_ci BEGIN
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
		ELSE 
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
			SET _OUT = CONCAT("{'starting_player':", _player1, ",'field':", _field, "}");
		ELSE
			SET _OUT = CONCAT("{'starting_player':", _player1, ",'field':'NULL'}");
		END IF;
        RETURN _out;
	ELSE
		INSERT INTO multiplayer_queue(player_code, game_code) VALUES(_playerCode, _gameCode) ; 
		RETURN "in_queue";
	END IF ;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `current_matches`
--

DROP TABLE IF EXISTS `current_matches`;
CREATE TABLE `current_matches` (
  `id` int(11) NOT NULL,
  `player_code_1` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `player_code_2` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `game_code` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `field` varchar(17) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `last_move`
--

DROP TABLE IF EXISTS `last_move`;
CREATE TABLE `last_move` (
  `id` int(11) NOT NULL,
  `game_id` int(11) NOT NULL,
  `player_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `move` varchar(10) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `multiplayer_queue`
--

DROP TABLE IF EXISTS `multiplayer_queue`;
CREATE TABLE `multiplayer_queue` (
  `id` int(11) NOT NULL,
  `player_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `game_code` varchar(10) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `current_matches`
--
ALTER TABLE `current_matches`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `last_move`
--
ALTER TABLE `last_move`
  ADD PRIMARY KEY (`id`),
  ADD KEY `game_id` (`game_id`);

--
-- Indexes for table `multiplayer_queue`
--
ALTER TABLE `multiplayer_queue`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `current_matches`
--
ALTER TABLE `current_matches`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `last_move`
--
ALTER TABLE `last_move`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `multiplayer_queue`
--
ALTER TABLE `multiplayer_queue`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `last_move`
--
ALTER TABLE `last_move`
  ADD CONSTRAINT `last_move_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `current_matches` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
