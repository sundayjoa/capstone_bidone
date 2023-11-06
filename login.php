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
	$password = $_POST["password"];

	$sql = "SELECT * FROM membership WHERE BINARY userID = ? AND BINARY userPW = ?"; 
	$stmt = $conn->prepare($sql);


	$stmt->bind_param("ss", $id, $password);


	$stmt->execute();

	$result = $stmt->get_result();

	if (mysqli_num_rows($result) > 0) { 
    	$row = mysqli_fetch_assoc($result); 
  	  $userName = $row["userName"]; 
  	  // 일치하는 계정이 있는 경우 userName과 true 보내기
  	  $response = array(
  	      "success" => true,
  	      "userName" => $userName
 	   );
   	 echo json_encode($response); 
	} else {
   	 // 일치하는 계정이 없는 경우 false 보내기
  	  $response = array(
   	     "success" => false
 	   );
  	  echo json_encode($response); 
	}

	mysqli_close($conn);
?>


