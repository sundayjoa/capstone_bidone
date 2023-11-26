<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if ($conn->connect_error) {
    	die("Connection failed: " . $conn->connect_error);
	}

	$request_number = $_GET['requestNumber'];
	$sender_id = $_GET['senderId'];
	$receiver_id = $_GET['receiverId'];

	$sql = "SELECT * FROM request_note WHERE request_number = '$request_number' AND ((sender_id = '$sender_id' AND 	receiver_id = '$receiver_id') OR (sender_id = '$receiver_id' AND receiver_id = '$sender_id'))";

	$result = $conn->query($sql);

	$rows = array();
	if ($result->num_rows > 0) {
    	while ($row = $result->fetch_assoc()) {
       	 // 검사하여 모든 조건을 만족하는 행만 추가
       	 if ((($row['sender_id'] == $sender_id || $row['sender_id'] == $receiver_id) && ($row['receiver_id'] == $sender_id 	|| $row['receiver_id'] == $receiver_id))) {
            $rows[] = $row;
       	 }
    	}
	}

	echo json_encode($rows);
	$conn->close();
?>