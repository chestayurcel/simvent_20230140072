<?php
// assets/create.php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn);

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed"]);
    exit();
}

// --- LOGIKA HYBRID INPUT (JSON & FORM) ---
$input = json_decode(file_get_contents('php://input'), true);

$name      = $input['asset_name'] ?? $_POST['asset_name'] ?? '';
$qty       = $input['qty'] ?? $_POST['qty'] ?? '';
$unit      = $input['unit'] ?? $_POST['unit'] ?? '';
$condition = $input['condition'] ?? $_POST['condition'] ?? '';
$date      = $input['entry_date'] ?? $_POST['entry_date'] ?? '';
$desc      = $input['item_desc'] ?? $_POST['item_desc'] ?? '';
$room_id   = $input['room_id'] ?? $_POST['room_id'] ?? '';

// Validasi input
if (empty($name) || empty($qty) || empty($room_id) || empty($date)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nama, Qty, Ruangan, dan Tanggal wajib diisi"]);
    exit();
}

try {
    $sql = "INSERT INTO Assets (asset_name, qty, unit, `condition`, entry_date, item_desc, room_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$name, $qty, $unit, $condition, $date, $desc, $room_id]);
    
    http_response_code(201);
    echo json_encode(["status" => "success", "message" => "Aset berhasil ditambahkan"]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>