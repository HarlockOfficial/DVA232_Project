<?php
    require_once("query.php");
    if(isset($_POST['feedback_id']) && !empty($_POST['feedback_id'])){
        $sql = "delete from feedback where id=:feedback_id";
        $arr[":feedback_id"] = $_POST['feedback_id'];
        query($sql, $arr);
    }
    function get_all_feedback(){
        $sql = "select id, message from feedback";
        return query($sql, null)->fetchAll(PDO::FETCH_ASSOC);
    }
    $list = get_all_feedback();
?>
<!DOCTYPE html>
<html>
    <head>
        <title>App Feedback</title>
    </head>
    <body>
        <?php
            for($list as $key=>$value){
                $string = "<form action='".$_SERVER['PHP_SELF']."' method='post'>"
                $string .= "<p>".$value['message']."</p>"
                $string .= "<input type='hidden' name='feedback_id' value='".$value['id']."' />"
                $string .= "<input type='submit' value='Delete the comment' />"
                $string .= "</form><hr />"
                echo $string;
            }
        ?>
    </body>
</html> 