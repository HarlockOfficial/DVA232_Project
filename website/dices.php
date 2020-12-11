<?php
	function dices($count=2){
		$out = 0
		for($i=0;$i<$count;$i++){
			$out+=random(6);
		}
		return json_encode(["response"=>$out]);
	}
?>