
-- --------------------------------------------------------

--
-- Table structure for table `current_matches`
--

DROP TABLE IF EXISTS `current_matches`;
CREATE TABLE IF NOT EXISTS `current_matches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_code_1` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `player_code_2` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `game_code` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `field` varchar(17) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
