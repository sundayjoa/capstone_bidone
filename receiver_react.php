<?php

	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if ($conn->connect_error) {
    	die("Connection failed: " . $conn->connect_error);
	}

	$request_number = $_GET['requestNumber'];
	$receiver_id = $_GET['receiverId'];
	$sender_id = $_GET['senderId'];

	$sql = "UPDATE request_note SET receiver_react = 'true' WHERE request_number = '$request_number' AND receiver_id = 	'$receiver_id' AND sender_id = '$sender_id'";

	if ($conn->query($sql) === TRUE) {
   	 echo "Record updated successfully";
	} else {
   	 echo "Error updating record: " . $conn->error;
	}

$conn->close();
?>