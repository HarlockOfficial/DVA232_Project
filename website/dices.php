<?php
	function dices($count=2){
		$out = array();
		for($i=0;$i<$count;$i++){
			array_push($out, random(6));
		}
		return json_encode(["response"=>$out]);
	}
?>