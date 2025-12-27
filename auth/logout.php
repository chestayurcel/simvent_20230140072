<?php
// auth/logout.php

// 1. Load Database
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

// Set Header JSON
header('Content-Type: application/json');

// Pastikan method adalah POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed"]);
    exit();
}

// 2. PROSES LOGOUT
try {
    // Ambil token dari header
    $token = getBearerToken();

    if (!$token) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Token tidak ditemukan"]);
        exit();
    }

    // Hapus token dari database
    $stmt = $conn->prepare("DELETE FROM UserTokens WHERE token = ?");
    $stmt->execute([$token]);

    // Cek apakah ada baris yang terhapus?
    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "Logout berhasil. Sesi telah dihapus."
        ]);
    } else {
        // Jika tidak ada yang terhapus, mungkin token sudah expired duluan atau salah
        // Tetap kembalikan sukses agar Android tidak bingung (Idempotent)
        echo json_encode([
            "status" => "success",
            "message" => "Sesi sudah berakhir sebelumnya."
        ]);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>