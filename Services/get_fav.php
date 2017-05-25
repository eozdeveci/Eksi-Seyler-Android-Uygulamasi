<?php
 
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_config.php';

$db = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_error) 
{
    die("Connection failed: " . $db->connect_error);
}

$sql = "SELECT * FROM favorite order by ID desc";
    
$result = $db->query($sql);
 
if ($result->num_rows > 0) {
    // looping through all results
    // products node
    $response["favori"] = array();

    while($row = mysqli_fetch_array($result)) {
        // temp user array
        $fav = array();
        $fav["ID"] = $row["ID"];
        $fav["Url"] = $row["Url"];
        $fav["Pic_Url"] = $row["Pic_Url"];
        $fav["Title"] = $row["Title"];
        $fav["Stats"] = $row["Stats"];
 
        // push single product into final response array
        array_push($response["favori"], $fav);
    }
    // success
    $response["success"] = 1;
    
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "Favori bulunamadi!";
 
    // echo no users JSON
    echo json_encode($response);
}
?>