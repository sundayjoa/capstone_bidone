<?php

$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

$work_number = $_POST['work_number'];
$consumerID = $_POST['consumerID'];
$consumerName = $_POST['consumerName'];
$address = $_POST['address'];

$sql = "UPDATE payinfo SET consumerID=?, consumerName=?, address=? WHERE work_number=?";

// 쿼리를 준비하고 바인드
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssss", $consumerID, $consumerName, $address, $work_number);

// 쿼리 실행
if ($stmt->execute()) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . $stmt->error;
}

// 연결 종료
$stmt->close();
$conn->close();
?>