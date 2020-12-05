<?php
    if($_SERVER['REQUEST_METHOD'] != "GET"){
		die(json_encode(["response"=>"method not valid"]));
    }
    require_once("bots.php");
    switch($_GET['bot']){
        case "rps":
            echo rps();
            break;
        default:
            echo json_encode(["response"=>"request parameter not valid"]);
    }
?>