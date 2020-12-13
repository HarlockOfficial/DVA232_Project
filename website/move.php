<?php
	//symbol = o|x
	//x = 0|1|2
	//y = 0|1|2
	//$move = array(x,y,symbol)
	//$move = "rock"|"paper"|"scissors"
	//$move = <number of dices>
	function add_move($game_code, $player_code, $move){
		require "query.php";
		$sql = "select add_move(:player, :game, :position, :move) as move";
		$arr[':player'] = $player_code;
		$arr[':game'] = $game_code;
		if($game_code == "ttt"){
			$arr[':position'] = 3*$move[0]+$move[1];
			$arr[':move'] = $move[2];
		}else if($game_code == "rps"){
			$arr[':position'] = 0;
			$arr[':move'] = $move;
		}else if($game_code == "dice"){
			$arr[':position'] = 0;
			$arr[':move'] = $move;
		}else{
			return json_encode(["response"=>"request parameter not valid"]);
		}
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['move'];
		return json_encode(["response"=>$ret]);
	}
	function get_move($game_code, $player_code){
		require "query.php";
		$sql = "select get_move(:player, :game) as move";
		$arr[':player']=$player_code;
		$arr[':game']=$game_code;
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['move'];
		if($game_code=="ttt" && $ret!="request parameter not valid"){
			$response = check_field(explode(",",$ret));
			$tmp['field']=$ret;
			$tmp['winner']="";
			if($response!="ok"){
				$sql = "delete from current_matches where game_code=:code and (player_code_1=:player or player_code_2=:player)";
				query($sql, array(":code"=>$game_code, ":player"=>$player_code));
				$tmp['winner']=$response;
			}
			$ret = json_encode($tmp);
		}
		return json_encode(["response"=>$ret]);
	}
	
	//used for tic tac toe
	function check_field($field){
		for($i=0;$i<3;$i++){
			//check each row
			if($field[3*$i]==$field[3*$i+1] && $field[3*$i+1]==$field[3*$i+2]){
				return $field[3*$i];
			}
			//check each column
			if($field[$i]==$field[3+$i] && $field[3+$i]==$field[6+$i]){
				return $field[$i];
			}
		}
		//check diagonal
		if($field[0]==$field[4] && $field[4]==$field[8]){
			return $field[0];
		}
		//check anti-diagonal
		if($field[2]==$field[4] && $field[4]==$field[6]){
			return $field[2];
		}
		return "ok";
	}
?>