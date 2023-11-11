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
$courier = $_POST['courier'];
$invoice_number = $_POST['invoice_number'];

$sql = "UPDATE payinfo SET courier=?, invoice_number=? WHERE work_number=?";

// 쿼리를 준비하고 바인드
$stmt = $conn->prepare($sql);
$stmt->bind_param("sss", $courier, $invoice_number, $work_number);

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