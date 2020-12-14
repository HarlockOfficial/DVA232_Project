
-- --------------------------------------------------------

--
-- Table structure for table `multiplayer_queue`
--

DROP TABLE IF EXISTS `multiplayer_queue`;
CREATE TABLE IF NOT EXISTS `multiplayer_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `game_code` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
