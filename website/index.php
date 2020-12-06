<?php
    if($_SERVER['REQUEST_METHOD'] != "GET"){
		die(json_encode(["response"=>"method not valid"]));
    }
	//code to add player to match-making queue
	//code to check if player still in queue (in case set bot as enemy)
	//code to add player move (if against bot, call bot move)
	//code to get player move
?>