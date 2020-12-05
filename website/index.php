<?php
    if($_SERVER['REQUEST_METHOD'] != "GET"){
		die(json_encode(["response"=>"method not valid"]));
    }
    require_once("bots.php");
    switch($_GET['bot']){
        case "rps":
            echo rps();
            break;
		case "dices":
			if(isset($_GET['count']) && !empty($_GET['count'])){
				echo dices($_GET['count']);
			}else{
				echo dices();
			}
			break;
		case "ttt":
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