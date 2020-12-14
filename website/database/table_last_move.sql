
-- --------------------------------------------------------

--
-- Table structure for table `last_move`
--

DROP TABLE IF EXISTS `last_move`;
CREATE TABLE IF NOT EXISTS `last_move` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game_id` int(11) NOT NULL,
  `player_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `move` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `game_id` (`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
