<?php

$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}


// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// work_number 받기
$work_number = $_POST['work_number'];

$sql = "SELECT * FROM payinfo WHERE work_number = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $work_number);
$stmt->execute();
$result = $stmt->get_result();

$response = array();

// work_number가 테이블에 존재하는지 확인
if ($result->num_rows > 0) {
    $response['exists'] = true;
} else {
    $response['exists'] = false;
}

// JSON 형식으로 결과 응답
header('Content-Type: application/json');
echo json_encode($response);

$stmt->close();
$conn->close();

?>