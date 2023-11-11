<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);

	$id = $_POST["id"];

	$sql = "SELECT * FROM membership WHERE BINARY userID = '$id'";
	$result = mysqli_query($conn, $sql);

	if (mysqli_num_rows($result) > 0) {
   	 $message = "exist";
	} else {
   	 $message = "true";
	}

	header('Content-Type: application/json; charset=utf-8'); 
	echo json_encode($message);

	mysqli_close($conn);
?>

