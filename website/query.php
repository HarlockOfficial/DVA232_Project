<?php
	function query($sql, $arr){
		require_once("db_credentials.php");
		$dbh = new PDO("mysql:host=$db_host;dbname=$db_name",$db_user,$db_pass);
		$stmt = $dbh->prepare($sql);
		$stmt->execute($arr);
		return $stmt;
	}
?>