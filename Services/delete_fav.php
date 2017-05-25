<?php
 
/*
 * Following code will delete a product from table
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['url'])) {
    $url = $_POST['url'];
 
    // include db connect class
    require_once __DIR__ . '/db_config.php';

    $db = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
    if ($db->connect_error) {

    die("Connection failed: " . $db->connect_error);
} 
 
    $url = $db->real_escape_string($url);
    // mysql update row with matched pid
    $result = $db->query("DELETE FROM favorite WHERE Url = '$url' ");
 
    // check if row deleted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Favori Silindi!";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "Favori Silinemedi!";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 2;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}

?>