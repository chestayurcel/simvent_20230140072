<?php
// rooms/create.php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn);

// Hanya method POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed"]);
    exit();
}

// --- LOGIKA HYBRID INPUT (Agar bisa baca JSON dari Android) ---
$nama = '';
$desc = '';

// 1. Cek jika dikirim via Form-Data (Postman/Web)
if (isset($_POST['room_name'])) {
    $nama = $_POST['room_name'];
    $desc = $_POST['room_desc'] ?? '';
} 
// 2. Cek jika dikirim via Raw JSON (Android Retrofit)
else {
    $inputJSON = json_decode(file_get_contents('php://input'), true);
    $nama = $inputJSON['room_name'] ?? '';
    $desc = $inputJSON['room_desc'] ?? '';
}

// Validasi Kosong
if (empty($nama)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nama ruangan wajib diisi"]);
    exit();
}

// Eksekusi Query
try {
    $stmt = $conn->prepare("INSERT INTO Rooms (room_name, room_desc) VALUES (?, ?)");
    if ($stmt->execute([$nama, $desc])) {
        http_response_code(201);
        echo json_encode(["status" => "success", "message" => "Ruangan berhasil dibuat"]);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>