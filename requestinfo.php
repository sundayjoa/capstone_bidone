<?php

	$con = mysqli_connect("127.0.0.1:3306", "root", "", "bidone");
	mysqli_query($con, 'SET NAMES utf8');

	$request_number = $_POST["request_number"];
	error_log("requestnumber: " . $request_number);

	$userID = $_POST["userID"];
	$userName = $_POST["userName"];
	$title = $_POST["title"];
	$content = $_POST["content"];
	$hope_purchase = $_POST["hope_purchase"];
	$upload_date = $_POST["upload_date"];

	$statement = mysqli_prepare($con, "INSERT INTO request VALUES(?,?,?,?,?,?,?)");

	mysqli_stmt_bind_param($statement,"sssssss",$request_number,$userID,$userName,$title, $content, $hope_purchase, $upload_date);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>