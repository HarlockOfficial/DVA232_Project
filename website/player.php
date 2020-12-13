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
		$sql = "select get_player(:player, :code) as json";
		$arr[':player']=$player_code;
		$arr[':code']=$game_code;
		$ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['json'];
		// code for testing purposes, remove later
		if($ret=="in_queue"){
			//add bot
			//'new_player' return is in the form {'starting_player':'<player_code>','field':''<field>'}
			$ret = json_decode(new_player("bot",$game_code))['response'];
			if($ret['starting_player']=="bot"){
				require_once("bots.php");
				$data = "";
				if($game_code=="dices"){
					$data = 6;
				}
				//setting move to the db
				$move = json_decode(bot($game_code, $data))['response'];
				$sql = "select add_move(:player, :game_code, :position, :move) as move";
				$arr[':player']="bot";
				$arr[':game_code']=$game_code;
				$arr[':position']=0;
				$arr['move']=$move;
				if($game_code=="ttt"){
					$arr[':position']=3*$move['x']+$move['y'];
					$arr[':move']='o';
					$tmp = explode(",", $ret['field']);
					$tmp[$arr[':position']]='o';
					$ret['field']=implode(",", $tmp);
				}
				query($sql, $arr);
				//impossible to fail
			}
		}
		// ---------------------------------
		return json_encode(["response"=>$ret]);
	}
?>