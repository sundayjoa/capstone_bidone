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

$userID = $_POST['userID'];


$sql = "SELECT COUNT(*) as count
        FROM payinfo
        WHERE work_number IN (
            SELECT work_number FROM board WHERE userID = ?
        ) AND (courier IS NULL OR invoice_number IS NULL)";

// Prepared statement 준비
if ($stmt = $conn->prepare($sql)) {
    $stmt->bind_param("s", $userID);


    $stmt->execute();


    $result = $stmt->get_result();
    $row = $result->fetch_assoc();


    echo json_encode(array("count" => $row['count']));


    $stmt->close();
} else {

    echo json_encode(array("error" => "Failed to prepare the SQL statement."));
}

// 데이터베이스 연결 종료
$conn->close();
?>