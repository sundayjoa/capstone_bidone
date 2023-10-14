<?php

	$con = mysqli_connect("127.0.0.1:3306", "root", "", "bidone");
	mysqli_query($con, 'SET NAMES utf8');

	$userName = $_POST["userName"];
	$userID = $_POST["userID"];
	$userPW = $_POST["userPW"];
	$userBirth = $_POST["userBirth"];
	$userphone = $_POST["userphone"];
	

	$statement = mysqli_prepare($con, "INSERT INTO membership VALUES(?,?,?,?,?)");

	mysqli_stmt_bind_param($statement,"sssss",$userName, $userID, $userPW, $userBirth, $userphone);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>