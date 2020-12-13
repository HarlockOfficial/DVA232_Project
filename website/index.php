<?php
	if($_SERVER['REQUEST_METHOD'] != "GET" && (!isset($_GET['action']) || empty($_GET['action'])) &&
		(!isset($_GET['game']) || empty($_GET['game'])) && (!isset($_GET['player']) || empty($_GET['player']))
	){
		echo json_encode(["response"=>"method not valid"]);
		exit(-1);
    }
	require_once("player.php");
	require_once("move.php");
	$player_code = $_GET['player'];
	$game_code = $_GET['game'];
	switch($_GET['action']){
		case 'add_queue': 	//add to multiplayer queue
			echo new_player($player_code, $game_code);
			break;
		case 'get_queue':	//get status (if still in multiplayer queue or not)
			echo get_player($player_code, $game_code);
			break;
		case 'add_move':	//insert player move
			if(!isset($_GET['move']) || empty($_GET['move'])){
				echo json_encode(["response"=>"request parameter not valid"]);
				die();	
			}
			$move = $_GET['move'];
			if($game_code == "ttt"){
				//in order: x,y,symbol
				$move = explode(",", $move);
			}
			echo add_move($player_code, $game_code, $move);
			break;
		case 'get_move':	//get opponent move (eventually with win or lose just for tic tac toe)
			echo get_move($player_code, $game_code);
			break;
		default:
			echo json_encode(["response"=>"request parameter not valid"]);
	}
?>