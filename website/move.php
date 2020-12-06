<?php
	//send using your code
	function add_move($game_code, $player_code, $move){
		require "query.php";
		$sql = "insert into last_move('player_code','game_code','move') values (:player, :game, :move)";
		$arr[':player']=$player_code;
		$arr[':game']=$game_code;
		$arr[':move']=$move;
		query($sql, $arr);
		return json_encode(["response"=>"ok"]);
	}
	//get using enemy code
	function get_move($game_code, $player_code){
		require "query.php";
		$sql = "select id,move from last_move where player_code=:player and game_code=:game order by id desc limit 1";
		$arr[':player']=$player_code;
		$arr[':game']=$game_code;
		$stmt = query($sql, $arr);
		if($stmt->rowCount()>0){
			$stmt = $stmt->fetch(PDO::FETCH_ASSOC);
			$sql = "delete from last_move where id=:id";
			$arr=array(":id"=>$stmt['id']);
			query($sql, $arr);
			return json_encode(["response"=>$stmt['move']]);
		}
		return json_encode(["response"=>null]);
	}
?>