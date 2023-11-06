<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	$userID = $_POST['userID'];

	$sql = "SELECT work_number, title, simple_explanation, upload_date, userName, thumbnail, detail_explanation, start, 		increase, date, time, finish_date, userID
		FROM board 
		WHERE userID = ?
		ORDER BY upload_date DESC";

	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $userID);
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
