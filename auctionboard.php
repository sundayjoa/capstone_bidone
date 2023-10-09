<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if ($conn->connect_error) {
    	die("Connection failed: " . $conn->connect_error);
	}

	$sql = "SELECT work_number, title, simple_explanation, upload_date, userName, thumbnail, detail_explanation, start, increase, date, time FROM board ORDER BY upload_date DESC";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
   	 $rows = array();
	
 	   while ($row = $result->fetch_assoc()) {
  	      $rows[] = $row;
 	   }

  	  echo json_encode($rows);
	} else {
  	  echo json_encode(null);
	}

	$conn->close();
?>