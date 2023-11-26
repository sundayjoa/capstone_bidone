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


	$sql = "SELECT p.work_number, p.consumerID, p.consumerName, p.price, p.address, p.courier, p.invoice_number,
                   IF(CHAR_LENGTH(p.work_number) = 8, r.title, b.title) AS title, 
                   m.userphone
            FROM payinfo p
            LEFT JOIN request r ON p.work_number = r.request_number AND CHAR_LENGTH(p.work_number) = 8
            LEFT JOIN board b ON p.work_number = b.work_number AND CHAR_LENGTH(p.work_number) = 10
            INNER JOIN membership m ON p.consumerID = m.userID
            WHERE p.sellerID = ?
            ORDER BY b.finish_date DESC";

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
