<?php
	$host = "127.0.0.1:3306";
	$user = "root";
	$password = "";
	$database = "bidone";

	$conn = new mysqli($host, $user, $password, $database);

	if ($conn->connect_error) {
    	die("Connection failed: " . $conn->connect_error);
	}

	$user_id = $conn->real_escape_string($_GET['userId']); // 안드로이드 스튜디오에서 전달받은 userId

    $sql = "SELECT rn.request_number, rn.title, rn.sender_id, rn.sender_name, rn.receiver_id, rn.receiver_name, rn.hope_purchase, 
       CASE 
           WHEN rn.content IS NULL AND rn.picture IS NOT NULL THEN '(사진)' 
           ELSE rn.content 
       END AS content, 
       rn.send_time, unread.unreadCount
FROM request_note rn
INNER JOIN (
    SELECT request_number, 
           LEAST(sender_id, receiver_id) AS participant1, 
           GREATEST(sender_id, receiver_id) AS participant2, 
           MAX(send_time) as MaxTime
    FROM request_note
    WHERE receiver_id = '$user_id' OR sender_id = '$user_id'
    GROUP BY request_number, LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)
) grouped_rn ON rn.request_number = grouped_rn.request_number 
              AND LEAST(rn.sender_id, rn.receiver_id) = grouped_rn.participant1 
              AND GREATEST(rn.sender_id, rn.receiver_id) = grouped_rn.participant2 
              AND rn.send_time = grouped_rn.MaxTime
LEFT JOIN (
    SELECT request_number, 
           LEAST(sender_id, receiver_id) AS participant1, 
           GREATEST(sender_id, receiver_id) AS participant2, 
           COUNT(*) AS unreadCount
    FROM request_note
    WHERE receiver_id = '$user_id' AND receiver_react IS NULL
    GROUP BY request_number, LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)
) unread ON rn.request_number = unread.request_number 
          AND LEAST(rn.sender_id, rn.receiver_id) = unread.participant1 
          AND GREATEST(rn.sender_id, rn.receiver_id) = unread.participant2
ORDER BY rn.send_time DESC";

    $result = $conn->query($sql);

    $rows = array();
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $rows[] = $row;
        }
    }

    echo json_encode($rows);
    $conn->close();
?>