<?php
/**
 * Created by PhpStorm.
 * User: jewelmahmudnimulshamim
 * Date: 2019-06-02
 * Time: 00:42
 */

echo "DO";

if(isset($_GET['send_notification'])){
    echo "Ok";
    send_notification ();
}

function send_notification(){
    echo 'Hello';
    define( 'API_ACCESS_KEY', 'YOUR API KEY HERE');
    //   $registrationIds = ;
#prep the bundle
    $msg = array(
        'body' 	=> $_GET['message'],
        'title'	=> $_GET['title'],

    );
    $fields = array(
        'to'		=> $_REQUEST['token'],
        'notification'	=> $msg
    );


    $headers = array
    (
        'Authorization: key=' . "AAAAdc9qsQQ:APA91bHpH9hi2_jXrOTl05FiWO8NzL6WZmOZvspWyaNqZgte1fX6Ss0JsPbjKbzA4dcnUxRW69hSg5vGIstcAB5l2P5uUYujomIkGS0kQ-byYXwA5QehZZEMGFWSrm-39gF9YSP2_rDe",
        'Content-Type: application/json'
    );
#Send Reponse To FireBase Server
    $ch = curl_init();
    curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
    curl_setopt( $ch,CURLOPT_POST, true );
    curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
    curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
    curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
    curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
    $result = curl_exec($ch );
    echo $result;
    curl_close( $ch );
}
?>