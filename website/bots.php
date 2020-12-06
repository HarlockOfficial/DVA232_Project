<?php
	//TODO fix
    require_once("bot_base.php");
	switch($_GET['bot']){
        case "rps":
			require_once("rock_paper_scissors.php");
            echo rps();
            break;
		case "dices":
			require_once("dices.php");
			if(isset($_GET['count']) && !empty($_GET['count'])){
				echo dices($_GET['count']);
			}else{
				echo dices();
			}
			break;
		case "ttt":
			require_once("tic_tac_toe.php");
			if(isset($_GET['field']) && !empty($_GET['field'])){
				echo ttt($_GET['field']);
			}else{
				echo ttt();
			}
			break;
        default:
            echo json_encode(["response"=>"request parameter not valid"]);
    }
?>