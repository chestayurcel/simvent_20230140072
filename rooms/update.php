<?php
// rooms/update.php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn); 

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed"]);
    exit();
}

// --- LOGIKA HYBRID INPUT ---
$id   = '';
$nama = '';
$desc = '';

if (isset($_POST['room_id'])) {
    $id   = $_POST['room_id'];
    $nama = $_POST['room_name'] ?? '';
    $desc = $_POST['room_desc'] ?? '';
} else {
    $inputJSON = json_decode(file_get_contents('php://input'), true);
    $id   = $inputJSON['room_id'] ?? '';
    $nama = $inputJSON['room_name'] ?? '';
    $desc = $inputJSON['room_desc'] ?? '';
}

// Validasi
if (empty($id) || empty($nama)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "ID dan Nama Ruangan wajib diisi"]);
    exit();
}

try {
    // Cek dulu apakah ID ada?
    // Langsung update saja
    $stmt = $conn->prepare("UPDATE Rooms SET room_name = ?, room_desc = ? WHERE room_id = ?");
    $stmt->execute([$nama, $desc, $id]);
    
    // Cek apakah ada baris yang berubah/ditemukan
    if ($stmt->rowCount() > 0) {
        echo json_encode(["status" => "success", "message" => "Data ruangan berhasil diperbarui"]);
    } else {
        echo json_encode(["status" => "success", "message" => "Data diperbarui (atau tidak ada perubahan data)"]);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>