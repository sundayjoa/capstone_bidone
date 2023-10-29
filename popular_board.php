<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	$sql = "SELECT board.work_number, board.title, board.userID, board.userName, board.thumbnail, 	board.simple_explanation, board.category, board.detail_explanation, board. detail_image, board.date, board.time, 	board.start, board.increase, board.upload_date, board.finish_date, COUNT(bookmark.work_number) as bookmarkCount 
	FROM board 
	JOIN bookmark ON board.work_number = bookmark.work_number 
	GROUP BY bookmark.work_number 
	ORDER BY bookmarkCount DESC";

	$result = $conn->query($sql);
	$data = array();

	if ($result->num_rows > 0) {
    	while($row = $result->fetch_assoc()) {
     	   $data[] = $row;
    	}
	}

	echo json_encode($data);

	$conn->close();
?>
