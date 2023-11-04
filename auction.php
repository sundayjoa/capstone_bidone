<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if (!$conn) {
 	 die("Connection failed: " . mysqli_connect_error());
	}

	$work_number = $_POST['work_number'];
	$sellerID = $_POST['sellerID'];
	$consumerID = $_POST['consumerID'];
	$consumerName = $_POST['consumerName'];
	$price = $_POST['price'];
	$finish_date = $_POST['finish_date'];


	$sql_check = "SELECT * FROM auction WHERE work_number = '$work_number'";

	$result_check = mysqli_query($conn, $sql_check);

	$num_rows = mysqli_num_rows($result_check);


	if ($num_rows > 0) {
    
   	 $sql_update = "UPDATE auction SET sellerID = '$sellerID', consumerName = '$consumerName', consumerID = '$consumerID', price = '$price', finish_date = '$finish_date' WHERE work_number = '$work_number'";
 	   if (mysqli_query($conn, $sql_update)) {
        	echo json_encode(array("success" => true)); 
  	  } else {
      	  echo "Error: " . $sql_update . "<br>" . mysqli_error($conn);
   	 }
	} else {
  	  $sql_insert = "INSERT INTO auction (work_number, sellerID, consumerName, consumerID, price, finish_date) VALUES ('$work_number', '$sellerID', '$consumerName', '$consumerID', '$price', '$finish_date')";
    	if (mysqli_query($conn, $sql_insert)) {
        
      	  echo json_encode(array("success" => true));
   	 } else {
      	  echo "Error: " . $sql_insert . "<br>" . mysqli_error($conn);
    	}
	}

	mysqli_close($conn);
?>