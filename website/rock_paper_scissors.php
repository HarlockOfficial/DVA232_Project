<?php
	function rps(){
		$choices = array("rock","paper","scissors");
		return json_encode(["response" => $choices[random(3)]]);
	}
?>
