<?php
	function bot($game_code, $data){
		require_once("bot_base.php");
		switch($game_code){
			case "rps":
				require_once("rock_paper_scissors.php");
				return rps();
			case "dices":
				require_once("dices.php");
				if(!empty($data)){
					return dices($data);
				}
				return dices();
			case "ttt":
				require_once("tic_tac_toe.php");
				if(!empty(data)){
					return ttt(data);
				}
				return ttt();
			default:
				return json_encode(["response"=>"request parameter not valid"]);
		}
	}
?>