<?php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn); // Cek Token dulu

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $stmt = $conn->query("SELECT * FROM Rooms ORDER BY room_name ASC");
    echo json_encode(["status" => "success", "data" => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
}
?>