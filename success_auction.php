<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	$consumerID = $_POST['consumerID'];

	$sql = "SELECT board.work_number, board.title, board.userID, board.userName, board.thumbnail, 
		board.simple_explanation, board.upload_date, board.finish_date, auction.price
		FROM board 
		INNER JOIN auction ON board.work_number = auction.work_number 
		WHERE auction.consumerID = ? AND board.finish_date < NOW()
		ORDER BY board.finish_date ASC";

	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $consumerID);
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
