<?php
// auth/login.php

// 1. Panggil Library JWT & Database
require_once '../vendor/autoload.php'; // Load library Composer
require_once '../config/database.php'; // Load koneksi database
use Firebase\JWT\JWT; // Import class JWT

// 2. KUNCI RAHASIA
$secret_key = "simvent_gacor_banget_yang_buat_ucel_soalnya_uhuyy_2025";

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    // 3. MENANGKAP INPUT JSON
    $inputJSON = file_get_contents('php://input');
    $input = json_decode($inputJSON, TRUE);

    // Fallback: Jika bukan JSON, coba cek $_POST biasa (untuk testing di Postman Form-Data)
    $username = $input['username'] ?? $_POST['username'] ?? '';
    $password = $input['password'] ?? $_POST['password'] ?? '';

    // Validasi input kosong
    if(empty($username) || empty($password)){
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Username dan Password wajib diisi"]);
        exit();
    }

    // 4. LOGIKA LOGIN
    try {
        // Cari user berdasarkan username
        $stmt = $conn->prepare("SELECT * FROM Users WHERE username = ?");
        $stmt->execute([$username]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        // Verifikasi Password
        if ($user && password_verify($password, $user['password'])) {
            
            // A. MEMBUAT JWT
            $issuedAt = time();
            $expirationTime = $issuedAt + (24 * 60 * 60); // 24 Jam
            
            $payload = [
                'iss' => 'http://localhost/simvent_api',
                'iat' => $issuedAt,
                'exp' => $expirationTime,
                'data' => [
                    'user_id' => $user['user_id'],
                    'username' => $user['username']
                ]
            ];

            // Generate String JWT
            $jwt = JWT::encode($payload, $secret_key, 'HS256');

            // B. SIMPAN KE DATABASE
            $mysqlExpired = date('Y-m-d H:i:s', $expirationTime);

            $insert = $conn->prepare("INSERT INTO UserTokens (user_id, token, expires_at) VALUES (?, ?, ?)");
            $insert->execute([$user['user_id'], $jwt, $mysqlExpired]);

            // C. KIRIM RESPON JSON
            echo json_encode([
                "status" => "success",
                "message" => "Login Berhasil",
                "data" => [
                    "user_id" => $user['user_id'],
                    "full_name" => $user['full_name'],
                    "token" => $jwt,
                    "expired_at" => $mysqlExpired
                ]
            ]);

        } else {
            // Login Gagal (Password salah / User tidak ada)
            http_response_code(401);
            echo json_encode(["status" => "error", "message" => "Username atau Password salah!"]);
        }

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Terjadi kesalahan server: " . $e->getMessage()]);
    }
}
?>