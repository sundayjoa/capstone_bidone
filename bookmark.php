<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}
	$work_number = $_POST["work_number"];
	$uploader_id = $_POST["uploader_id"];
	$consumerID = $_POST["consumerID"];

	$query = "SELECT * FROM bookmark WHERE work_number = '$work_number' AND consumerID = '$consumerID'";
	$result = mysqli_query($conn, $query);

	if (mysqli_num_rows($result) > 0) {
   	 // 이미 존재하는 게시글일 때 삭제
   	 $query = "DELETE FROM bookmark WHERE work_number = '$work_number' AND consumerID = '$consumerID'";
   	 mysqli_query($conn, $query);
  	  echo "bookmark_removed";
	} else {
   	 // 추가
   	 $query = "INSERT INTO bookmark (work_number, uploader_id, consumerID) VALUES ('$work_number', '$uploader_id', '$consumerID')";
   	 mysqli_query($conn, $query);
  	  echo "bookmark_added";
	}

	mysqli_close($conn);
?>
