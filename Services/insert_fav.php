<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();


 
// check for required fields
if (isset($_POST['url']) && isset($_POST['pic_url']) && isset($_POST['title']) && isset($_POST['stats']) ) {
 
    $url = $_POST['url'];
    $pic_url = $_POST['pic_url'];
    $title = $_POST['title'];
    $stats = $_POST['stats'];
    // include db connect class
    //require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    //$db = new DB_CONNECT();

    require_once __DIR__ . '/db_config.php';

    $db = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

    if ($db->connect_error) {
    die("Connection failed: " . $db->connect_error);
    }

    $url = $db->real_escape_string($url);
    $pic_url = $db->real_escape_string($pic_url);
    $title = $db->real_escape_string($title);
 
    $sql = "SELECT * FROM favorite WHERE Url = '$url' AND Pic_Url = '$pic_url'";
    $result = $db->query($sql);

    if ($result->num_rows > 0) {

        $response["success"] = 2;
        $response["message"] = "Zaten favoriye eklenmiş!";
        echo json_encode($response);

    }
    else
    {
        // mysql inserting a new row
    $result = $db->query("INSERT INTO favorite(Url, Pic_Url, Title, Stats) VALUES('$url', '$pic_url', '$title', '$stats')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Favorite added successfully.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row

        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
    }
    
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    header('Content-Type: application/json');
    // echoing JSON response
    echo json_encode($response);
}
?>