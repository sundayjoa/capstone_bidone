<?php

$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}


$worknumber = $_POST['worknumber'];
$sellerID = $_POST['sellerID'];
$sellerName = $_POST['sellerName'];
$consumerID = $_POST['consumerID'];
$consumerName = $_POST['consumerName'];
$price = $_POST['price'];
$comaddress = $_POST['comaddress'];

$stmt = $conn->prepare("INSERT INTO payinfo (work_number, sellerID, sellerName, consumerID, consumerName, price, address) VALUES (?, ?, ?, ?, ?, ?, ?)");
$stmt->bind_param("sssssss", $worknumber, $sellerID, $sellerName, $consumerID, $consumerName, $price, $comaddress);

if ($stmt->execute()) {
    echo "New record created successfully";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();

?>