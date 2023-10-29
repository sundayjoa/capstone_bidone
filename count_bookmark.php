<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";
	$conn = mysqli_connect($host, $user, $password, $database);

	if (!$conn) {
   	 die("Connection failed: " . mysqli_connect_error());
	}

	$work_number = $_GET['work_number'];

	$sql = "SELECT COUNT(*) as count FROM bookmark WHERE work_number='$work_number'";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
   	 $row = $result->fetch_assoc();
 	   echo $row['count'];
	} else {
    	echo "0";
	}

	$conn->close();
?>
