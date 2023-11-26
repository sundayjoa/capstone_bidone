<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if ($conn->connect_error) {
    	die("Connection failed: " . $conn->connect_error);
	}

	$work_number = $_POST['request_number'];

// 데이터베이스 연결 확인 및 쿼리 실행
$stmt = $conn->prepare("SELECT sellerID, sellerName, consumerID, consumerName, address, price FROM payinfo WHERE work_number = ?");
$stmt->bind_param("s", $work_number);
$stmt->execute();
$result = $stmt->get_result();

$data = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

echo json_encode($data);
$conn->close();


?>