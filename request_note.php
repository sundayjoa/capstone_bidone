<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if (!$conn) {
 	 die("Connection failed: " . mysqli_connect_error());
	}

	$request_number = $_POST['request_number'];
	$title = $_POST['title'];
	$sender_id = $_POST['sender_id'];
	$sender_name = $_POST['sender_name'];
	$receiver_id = $_POST['receiver_id'];
	$receiver_name = $_POST['receiver_name'];
	$content = $_POST['content'];
	$hope_purchase = $_POST['hope_purchase'];
	$sender_react = $_POST['sender_react'];
	$send_time = $_POST['send_time'];


	$stmt = $conn->prepare("INSERT INTO request_note (request_number, title, sender_id, sender_name, receiver_id, receiver_name, content, hope_purchase, sender_react, send_time) VALUES (?,?,?,?,?,?,?,?,?,?)");
	$stmt->bind_param("ssssssssss", $request_number, $title, $sender_id, $sender_name, $receiver_id, $receiver_name, $content, $hope_purchase, $sender_react, $send_time);

	if ($stmt->execute()) {
  	  echo "success";
	} else {
  	  echo "Error: " . $stmt->error;
	}

	$stmt->close();
	$conn->close();
?>