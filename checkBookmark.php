<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	// POST로 전송된 데이터 가져오기
	$work_number = $_POST['work_number'];
	$consumerID = $_POST['consumerID'];

	// 해당 work_number와 consumerID로 북마크가 존재하는지 확인하는 쿼리
	$sql = "SELECT * FROM bookmark WHERE work_number='$work_number' AND consumerID='$consumerID'";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
    	// 북마크가 존재할 경우
   	 echo "exists";
	} else {
   	 // 북마크가 존재하지 않을 경우
   	 echo "not_exists";
	}

	$conn->close();
?>
