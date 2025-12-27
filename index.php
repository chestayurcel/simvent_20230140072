<?php
// index.php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Set zona waktu ke WIB (Penting agar jam di JSON akurat)
date_default_timezone_set('Asia/Jakarta');

echo json_encode([
    "app_name" => "SIMVENT API (Sistem Inventaris)",
    "version" => "1.0.0",
    "status" => "active",
    "server_time" => date("Y-m-d H:i:s"),
    "message" => "Server berjalan normal. Silakan akses endpoint /auth atau /assets.",
    "developer" => [
        "name" => "Chesta Yurcel Zebada",
        "nim" => "20230140072"
    ]
], JSON_PRETTY_PRINT);
?>