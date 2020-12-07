<?php
	function new_player($player_code, $game_code){
		require_once("query.php");
		$sql = "select new_player(:player, :code) as json";
		$arr[':player']=$player_code;
		$arr[':code']=$game_code;
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['json'];
		return json_encode(["response"=>$ret]);
	}
	function get_player($player_code, $game_code){
		require_once("query.php");
		$sql = "select get_player(:player, :code) as json"
		$arr[':player']=$player_code;
		$arr[':code']=$game_code;
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['json'];
		return json_encode(["response"=>$ret]);
	}
?>