<?php
    if($_SERVER['REQUEST_METHOD'] != "GET" && (!isset($_GET['message']) || empty($_GET['message']))){
        echo json_encode(["response"=>"method not valid"]);
        exit(-1);
    }
    if(strlen($_GET['message'])>1000){
        echo json_encode(["response"=>"message too long"]);    
        exit(-2);
    }
    require "query.php";
    $sql = "insert into feedback(message) values(:message);";
    $arr[":message"] = htmlentities($_GET['message']);
    query($sql, $arr);
    echo json_encode(["response"=>"ok"]);
?>