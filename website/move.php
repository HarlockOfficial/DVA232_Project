<?php
	//symbol = o|x
	//x = 0|1|2
	//y = 0|1|2
	//$move = array(position,symbol)
	//$move = "rock"|"paper"|"scissors"
	//$move = <number of dices>
	function add_move($player_code, $game_code, $move){
		require "query.php";
		$sql = "select add_move(:player, :game, :position, :move) as move";
		$arr[':player'] = $player_code;
		$arr[':game'] = $game_code;
		if($game_code == "ttt"){
			$arr[':position'] = (2*$move[0])-1;
			$arr[':move'] = $move[1];
		}else if($game_code == "rps"){
			$arr[':position'] = 0;
			$arr[':move'] = $move;
		}else if($game_code == "dices"){
			$arr[':position'] = $move;
			$arr[':move'] = "";
		}else{
			return json_encode(["response"=>"request parameter not valid, unknown game code"]);
		}
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['move'];
		return json_encode(["response"=>$ret]);
	}
function get_move($player_code, $game_code){
		require "query.php";
		$sql = "select get_move(:player, :game) as move";
		$arr[':player']=$player_code;
		$arr[':game']=$game_code;
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['move'];
		if($game_code=="ttt" && $ret!="request parameter not valid"){
			$ret = str_replace(",", "", $ret);
			$ret = str_split($ret);
			$sql = "update current_matches set field=:field where game_code=:game and (player_code_1=:player or player_code_2=:player)";
			$arr[':field']=implode(",",$ret);
			query($sql, $arr);
			$response = check_field($ret);
			$tmp['field']=implode(",",$ret);
			$tmp['winner']="";
			if($response!="ok"){
				$tmp['winner']=$response;
			}
			$ret = json_encode($tmp);
		}
		return json_encode(["response"=>$ret]);
	}
	
	//used for tic tac toe
	function check_field($field){
	    for($i=0;$i<9;$i++){
	        $field[$i] = $field[$i]=="-"?"":$field[$i];
	    }
		for($i=0;$i<3;$i++){
			//check each row
			if($field[3*$i]!="" && $field[3*$i+1]!="" && $field[3*$i+2]!="" && $field[3*$i]==$field[3*$i+1] && $field[3*$i+1]==$field[3*$i+2]){
				return $field[3*$i];
			}
			//check each column
			if($field[$i]!="" && $field[3+$i]!="" && $field[6+$i]!="" && $field[$i]==$field[3+$i] && $field[3+$i]==$field[6+$i]){
				return $field[$i];
			}
		}
		//check diagonal
		if($field[0]!="" && $field[4]!="" && $field[8]!=""&& $field[0]==$field[4] && $field[4]==$field[8]){
			return $field[0];
		}
		//check anti-diagonal
		if($field[2]!="" && $field[4]!="" && $field[6]!="" && $field[2]==$field[4] && $field[4]==$field[6]){
			return $field[2];
		}
		return "ok";
	}
?>