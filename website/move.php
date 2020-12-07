<?php
	//symbol = o|x
	//x = 0|1|2
	//y = 0|1|2
	//$move = array(x,y,symbol)
	//$move = "rock"|"paper"|"scissors"
	//$move = <number of dices>
	function add_move($game_code, $player_code, $move){
		require "query.php";
		if($game_code=="ttt"){
			$sql = "select field from current_matches where game_code=:game ".
					"and player_code=:player";
			$arr[':player']=$player_code;
			$arr[':game']=$game_code;
			$stmt = query($sql, $arr);
			if($stmt->rowCount()>0){
				$stmt = explode(",",$stmt->fetch(PDO::FETCH_ASSOC)['field']);
				$stmt[3*$move[0]+$move[1]]=$move[2];
				$sql = "update current_matches set field=:field where game_code=:game ".
					"and player_code=:player";
				$arr[':field']=implode(",",$stmt);
				query($sql, $arr);
				return json_encode(["response"=>check_field($stmt)]);
			}
			return json_encode(["response"=>"request parameter not valid"]);
		}else if($game_code=="dice"){
			//get quantity of dices, roll them and store the result to table
			//check if win
		}else if($game_code=="rps"){
			//get the player choice and store to last move table
			//check if win
			$sql = "select id from current_matches where game_code='rps' and (player_code_1 = :player or player_code_2 = :player)";
			$arr[':player']=$player_code;
			$stmt = query($sql, $arr);
			if($stmt->rowCount()>0){
				$stmt = $stmt->fetch(PDO::FETCH_ASSOC)['id'];
				
			}
			return json_encode(["response"=>"request parameter not valid"]);
		}
		return json_encode(["response"=>"request parameter not valid"]);
	}
	function get_move($game_code, $player_code){
		require "query.php";
		//TODO code not valid
		/*$sql = "select id,move from last_move where player_code=:player and game_code=:game order by id desc limit 1";
		$arr[':player']=$player_code;
		$arr[':game']=$game_code;
		$stmt = query($sql, $arr);
		if($stmt->rowCount()>0){
			$stmt = $stmt->fetch(PDO::FETCH_ASSOC);
			$sql = "delete from last_move where id=:id";
			$arr=array(":id"=>$stmt['id']);
			query($sql, $arr);
			return json_encode(["response"=>$stmt['move']]);
		}*/
		return json_encode(["response"=>null]);
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