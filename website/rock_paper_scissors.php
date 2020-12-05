<?php
	if ($_SERVER['REQUEST_METHOD'] != "GET") {
		die(json_encode(["response"=>"method not valid"]));
	}
	require_once("bot_base.php");
	$choices = array("rock","paper","scissors");
	echo json_encode(["response" => $choices[random(3)]]);
?>