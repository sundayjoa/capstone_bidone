<?php
    
    $host = "127.0.0.1:3306";
    $user = "root";
    $password = "";
    $database = "bidone";

    
    $conn = new mysqli($host, $user, $password, $database);
	
	if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $found = false;
    while (!$found) {
        $number = rand(1000000000, 9999999999);
        $sql = "SELECT * FROM request WHERE request_number=$number";
	$result = $conn->query($sql);
        if ($result->num_rows == 0) {
            // Number is not in use
            $found = true;
        }
    }

    echo $number;

    $conn->close();
?>