
--
-- Constraints for dumped tables
--

--
-- Constraints for table `last_move`
--
ALTER TABLE `last_move`
  ADD CONSTRAINT `last_move_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `current_matches` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
