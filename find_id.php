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

	$name = $_POST["name"];
	$birth = $_POST["birth"];

	$sql = "SELECT * FROM membership WHERE userName = ? AND userBirth = ?"; 
	$stmt = $conn->prepare($sql);


	$stmt->bind_param("ss", $name, $birth);


	$stmt->execute();

	$result = $stmt->get_result();

	if (mysqli_num_rows($result) > 0) { 
   	 $row = mysqli_fetch_assoc($result); 
   	 $userID = $row["userID"]; 
	 $id_length = strlen($userID);
	 $middle_start = floor(($id_length - 3) / 2);
   	 $maskedID = substr_replace($userID, str_repeat('*', 3), $middle_start, 3);
   	 // 아이디가 있는 경우 maskedID만 보내기
   	 echo json_encode($maskedID); 
	} else {
   	 // 비밀번호가 없는 경우 false 보내기
   	 echo json_encode(false); 
	}

	mysqli_close($conn);
?>


