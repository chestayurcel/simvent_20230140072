<?php
// utils/auth_helper.php

require_once '../vendor/autoload.php';
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$secret_key = "simvent_gacor_banget_yang_buat_ucel_soalnya_uhuyy_2025";

function getAuthorizationHeader(){
    $headers = null;
    if (isset($_SERVER['Authorization'])) {
        $headers = trim($_SERVER["Authorization"]);
    }
    else if (isset($_SERVER['HTTP_AUTHORIZATION'])) { 
        $headers = trim($_SERVER["HTTP_AUTHORIZATION"]);
    } elseif (function_exists('apache_request_headers')) {
        $requestHeaders = apache_request_headers();
        $requestHeaders = array_combine(array_map('ucwords', array_keys($requestHeaders)), array_values($requestHeaders));
        if (isset($requestHeaders['Authorization'])) {
            $headers = trim($requestHeaders['Authorization']);
        }
    }
    return $headers;
}

function getBearerToken() {
    $headers = getAuthorizationHeader();
    if (!empty($headers)) {
        if (preg_match('/Bearer\s(\S+)/', $headers, $matches)) {
            return $matches[1];
        }
    }
    return null;
}

// --- FUNGSI UTAMA: AUTHENTICATE ---
// Fungsi ini dipanggil di setiap file API (read, create, dll) untuk cek token
function authenticate($conn) {
    global $secret_key;

    $token = getBearerToken();

    // 1. Cek apakah ada token?
    if (!$token) {
        http_response_code(401);
        echo json_encode(["status" => "error", "message" => "Token tidak ditemukan. Silakan login."]);
        exit();
    }

    try {
        // 2. Cek Validitas Token (Signature & Expired)
        // Kalau expired/palsu, baris ini otomatis throw Exception
        $decoded = JWT::decode($token, new Key($secret_key, 'HS256'));

        // 3. Cek Database (Apakah token sudah dilogout?)
        $stmt = $conn->prepare("SELECT * FROM UserTokens WHERE token = ?");
        $stmt->execute([$token]);
        
        if ($stmt->rowCount() == 0) {
            throw new Exception("Sesi token tidak ditemukan di database (Logout)");
        }

        // Jika lolos semua, kembalikan data user
        return $decoded->data;

    } catch (Exception $e) {
        // Token Salah / Expired / Error Server
        http_response_code(401);
        echo json_encode([
            "status" => "error", 
            "message" => "Akses Ditolak: " . $e->getMessage()
        ]);
        exit();
    }
}
?>