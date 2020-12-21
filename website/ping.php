<?php
    if($_SERVER['REQUEST_METHOD'] != "GET" && (!isset($_GET['game']) || empty($_GET['game'])) && 
        (!isset($_GET['player']) || empty($_GET['player']))){
        echo json_encode(["response"=>"method not valid"]);
        exit(-1);
    }
    require_once("query.php");
    $sql = "select add_ping(:player, :game) as ping";
    $arr[":player"]=$_GET['player'];
    $arr[":game"]=$_GET['game'];
    $ret = query($sql, $arr)->fetch(PDO::FETCH_ASSOC)['ping'];
    echo json_encode(["response"=>$ret]);
?>