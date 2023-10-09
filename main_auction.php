<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	
	if ($conn->connect_error) {
  	die("Connection failed: " . $conn->connect_error);
	}

	$workNumber = $_POST['workNumber'];

	$sql = "
	SELECT price FROM auction
	WHERE work_number = $workNumber
	LIMIT 1;
	";

	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
	  while($row = $result->fetch_assoc()) {
 	   echo $row["price"];
 	 }
	} else {
 	 echo "0";
	}
	$conn->close();
?>