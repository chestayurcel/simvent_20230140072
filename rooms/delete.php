<?php
// rooms/delete.php

// --- 1. Debugging Mode (Wajib dimatikan saat production) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require_once '../config/database.php';
require_once '../utils/auth_helper.php';

// Validasi Token
authenticate($conn);

// Hanya terima method POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed. Gunakan POST."]);
    exit();
}

// --- 2. LOGIKA PENANGKAPAN INPUT (HYBRID) ---
$id = '';

// Coba ambil dari Form-Data (x-www-form-urlencoded)
if (isset($_POST['room_id'])) {
    $id = $_POST['room_id'];
} 
// Jika kosong, coba ambil dari Raw JSON
else {
    $inputJSON = json_decode(file_get_contents('php://input'), true);
    if (isset($inputJSON['room_id'])) {
        $id = $inputJSON['room_id'];
    }
}

// Jika ID masih kosong juga, tolak!
if (empty($id)) {
    http_response_code(400); // Bad Request
    echo json_encode([
        "status" => "error", 
        "message" => "Parameter 'room_id' tidak ditemukan. Pastikan key di Postman bernama 'room_id'."
    ]);
    exit();
}
// ------------------------------------------------

try {
    // --- 3. VALIDASI ASET (REQ-7) ---
    $check = $conn->prepare("SELECT count(*) as total FROM Assets WHERE room_id = ?");
    $check->execute([$id]);
    $result = $check->fetch(PDO::FETCH_ASSOC);

    // Debugging: Jika Anda ingin melihat apakah ID terbaca, uncomment baris bawah ini
    // var_dump($id, $result); exit();

    if ($result['total'] > 0) {
        http_response_code(400); // Bad Request
        echo json_encode([
            "status" => "error", 
            "message" => "GAGAL HAPUS! Ruangan ini masih berisi " . $result['total'] . " aset. Kosongkan aset terlebih dahulu."
        ]);
        exit();
    }

    // --- 4. EKSEKUSI HAPUS ---
    $del = $conn->prepare("DELETE FROM Rooms WHERE room_id = ?");
    $del->execute([$id]);

    if ($del->rowCount() > 0) {
        echo json_encode(["status" => "success", "message" => "Ruangan berhasil dihapus permanen."]);
    } else {
        // Jika SQL sukses tapi tidak ada baris yang dihapus (misal ID tidak ditemukan)
        echo json_encode(["status" => "error", "message" => "ID Ruangan tidak ditemukan di database."]);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>