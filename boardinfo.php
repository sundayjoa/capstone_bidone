<?php

	$con = mysqli_connect("127.0.0.1:3306", "root", "", "bidone");
	mysqli_query($con, 'SET NAMES utf8');
	$work_number = $_POST["worknumber"];
	error_log("worknumber: " . $work_number);

	$title = $_POST["title"];
	$userID = $_POST["userID"];
	$thumbnail = $_POST["thumbnail"];
	$simple_explanation = $_POST["simple_explanation"];
	$category = $_POST["category"];
	$detail_explanation = $_POST["detail_explanation"];
	$detail_image = $_POST["detail_image"];
	$date = $_POST["datetextView"];
	$time = $_POST["timetextView"];
	$start = $_POST["starttext"];
	$increase = $_POST["increasetext"];
	$upload_date = $_POST["upload"];

	$statement = mysqli_prepare($con, "INSERT INTO board VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

	mysqli_stmt_bind_param($statement,"sssssssssssss",$work_number,$title,$userID,$thumbnail,$simple_explanation,$category,$detail_explanation,$detail_image,$date,$time,$start,$increase,$upload_date);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>