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

	$sql = "SELECT * FROM membership WHERE userID = '$id'"; 
	$result = mysqli_query($conn, $sql);

	if (mysqli_num_rows($result) > 0) { 
   	 $row = mysqli_fetch_assoc($result); 
   	 $userPW = $row["userPW"]; 
	 $pw_length = strlen($userPW);
	 $middle_start = floor(($pw_length - 3) / 2);
   	 $maskedPW = substr_replace($userPW, str_repeat('*', 3), $middle_start, 3);
   	 // 비밀번호가 있는 경우 maskedPW만 보내기
   	 echo json_encode($maskedPW); 
	} else {
   	 // 비밀번호가 없는 경우 false 보내기
   	 echo json_encode(false); 
	}

	mysqli_close($conn);
?>


