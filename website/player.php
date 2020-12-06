<?php
	function new_player($player_code, $game_code){
		require_once("query.php");
		$sql = "select id, player_code from multiplayer_queue where game_code=:game ".
				"order by RAND() limit 1";
		$arr[":game"] = $game_code;
		$stmt = query($sql, $arr);
		if($stmt->rowCount()>0){
			$stmt = $stmt->fetch(PDO::FETCH_ASSOC);
			$sql = "delete from multiplayer_queue where id=:id";
			query($sql, array(":id"=>$stmt['id']));
			$sql = "insert into current_matches(player_code_1, player_code_2, game_code, ".
					"field) values(:player1, :player2, :game, :field)";
			$arr[":player1"]=(random(2)==1?$stmt['player_code']:$player_code);
			$arr[":player2"]=($arr[':player1']==$stmt['player_code']?$player_code:$stmt['player_code']);
			$arr["field"]=$game_code=="ttt"?"-,-,-,-,-,-,-,-,-":NULL;
			query($sql, $arr);
			require_once("bot_base.php");
			return json_encode(["response"=>array("starting_player"=>$arr[':player1'],
													"field"=>$arr[":field"])]);
		}else{
			$sql = "insert into multiplayer_queue('player_code','game_code') ".
					"values(:player, :game)";
			$arr[":player"]=$player_code;
			query($sql,$arr);
			return json_encode(["response"=>"in_queue"]);
		}
	}
	function get_player($player_code, $game_code){
		require_once("query.php");
		$sql = "select id form multiplayer_queue where player_code=:player and ".
				"game_code=:game"
		$arr[':player']=$player_code;
		$arr['game']=$game_code;
		if(query($sql, $arr)->rowCount()>0){
			return json_encode(["response"=>"in_queue"]);
		}else{
			$sql = "select player_code_1, field form current_matches where ".
					"game_code=:game and (player_code_1=:player or player_code_2=:player)";
			$stmt = query($sql, $arr);
			if($stmt->rowCount()>0){
				$stmt = $stmt->fetch(PDO::FETCH_ASSOC);
				return json_encode(["response"=>array("starting_player"=>$stmt['player_code_1'],
									"field"=>$stmt['field'])]);
			}else{
				return json_encode(["response"=>"request parameter not valid"]);
			}
		}
	}
?>