<?php
// assets/update.php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn);

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed"]);
    exit();
}

// --- LOGIKA HYBRID INPUT ---
$input = json_decode(file_get_contents('php://input'), true);

$id        = $input['asset_id'] ?? $_POST['asset_id'] ?? '';
$name      = $input['asset_name'] ?? $_POST['asset_name'] ?? '';
$qty       = $input['qty'] ?? $_POST['qty'] ?? '';
$unit      = $input['unit'] ?? $_POST['unit'] ?? '';
$condition = $input['condition'] ?? $_POST['condition'] ?? '';
$date      = $input['entry_date'] ?? $_POST['entry_date'] ?? '';
$desc      = $input['item_desc'] ?? $_POST['item_desc'] ?? '';
$room_id   = $input['room_id'] ?? $_POST['room_id'] ?? '';

if (empty($id)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Asset ID tidak ditemukan"]);
    exit();
}

try {
    $sql = "UPDATE Assets SET asset_name=?, qty=?, unit=?, `condition`=?, entry_date=?, item_desc=?, room_id=? WHERE asset_id=?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$name, $qty, $unit, $condition, $date, $desc, $room_id, $id]);
    
    echo json_encode(["status" => "success", "message" => "Aset berhasil diperbarui"]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Gagal update: " . $e->getMessage()]);
}
?>