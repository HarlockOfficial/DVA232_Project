<?php
	//completely not working
	function add_player($player_code, $game_code){
		require "query.php";
		$sql = "insert into multiplayer_queue('player_code','game_code') values(:player, :game)";
		$arr[":player"]=$player_code;
		$arr[":game"]=$game_code;
		query($sql,$arr);
		return json_encode(["response"=>"ok"]);
	}
	function get_player($game_code){
		require "query.php";
		$sql = "select id, player_code from multiplayer_queue where game_code=:game order by RAND() limit 1";
		$arr[":game"] = $game_code;
		$stmt = query($sql, $arr);
		if($stmt->rowCount()>0){
			$stmt = $stmt->fetch(PDO::FETCH_ASSOC);
			$sql = "delete from multiplayer_queue where id=:id";
			$arr=array(":id"=>$stmt['id']);
			query($sql, $arr);
			return $stmt['player_code'];
		}
		return "bot";
	}
?>