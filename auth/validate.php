<?php
// auth/validate.php

// 1. Load Library & Database
require_once '../vendor/autoload.php';
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$secret_key = "simvent_gacor_banget_yang_buat_ucel_soalnya_uhuyy_2025";

try {
    $token = getBearerToken();

    if (!$token) {
        throw new Exception("Token tidak ditemukan di Header");
    }

    // 1. Cek Stateless (Decode JWT)
    // Jika signature salah atau waktu di token sudah habis, library ini akan otomatis throw Exception
    $decoded = JWT::decode($token, new Key($secret_key, 'HS256'));

    // 2. Cek Stateful (Database)
    // Kita cek apakah admin sudah pernah logout (token dihapus dari DB)
    $stmt = $conn->prepare("SELECT * FROM UserTokens WHERE token = ? AND expires_at > NOW()");
    $stmt->execute([$token]);
    $tokenData = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($tokenData) {
        // Token Valid & Ada di Database
        echo json_encode([
            "status" => "success",
            "message" => "Token Valid",
            "data" => [
                "user_id" => $decoded->data->user_id,
                "username" => $decoded->data->username
            ]
        ]);
    } else {
        // Token Valid secara struktur, tapi tidak ada di DB (Mungkin sudah dilogout paksa)
        http_response_code(401);
        echo json_encode(["status" => "invalid", "message" => "Sesi tidak valid atau sudah kadaluarsa (DB)"]);
    }

} catch (Exception $e) {
    // Token Expired atau Signature Salah
    http_response_code(401);
    echo json_encode([
        "status" => "invalid", 
        "message" => "Token tidak valid: " . $e->getMessage()
    ]);
}
?>