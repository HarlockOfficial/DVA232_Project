<?php
	function query($sql, $arr){
	    try{
    		require "db_credentials.php";
    		$dbh = new PDO("mysql:host=$db_host;dbname=$db_name",$db_user,$db_pass);
    		$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    		$stmt = $dbh->prepare($sql);
    		$stmt->execute($arr);
    		return $stmt;
	    }catch(Exception $e){
	        print_r($e);
	    }
	}
?>