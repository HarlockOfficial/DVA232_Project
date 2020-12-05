<?php
	//expecting csv with 9 elements
	function ttt($field=""){
		if(isset($field) && !empty($field)){
			$tmp = explode(",", $field);
			$field=array();
			for($i=0;$i<3;$i++){
				for($j=0;$j<3;$j++){
					$field[$i][$j]=$tmp[3*$i+$j];
				}
			}
		}
		do{
			$x = random(3);
			$y = random(3);
			if(!isset($field) || empty($field)){
				break;
			}
		}while($field[$x][$y] != '');
		return json_encode(["response"=>["x"=>$x, "y"=>$y]]);
	}
?>