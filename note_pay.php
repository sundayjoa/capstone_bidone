<?php

$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}


$worknumber = $_POST['request_number'];
$sellerID = $_POST['sellerID'];
$sellerName = $_POST['sellerName'];
$price = $_POST['hope_purchase'];

$stmt = $conn->prepare("INSERT INTO payinfo (work_number, sellerID, sellerName, price) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssss", $worknumber, $sellerID, $sellerName, $price);

if ($stmt->execute()) {
    echo "New record created successfully";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();

?>