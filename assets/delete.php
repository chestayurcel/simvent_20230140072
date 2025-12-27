<?php
// assets/delete.php
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
$id = $input['asset_id'] ?? $_POST['asset_id'] ?? '';

if (empty($id)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Asset ID diperlukan"]);
    exit();
}

try {
    $stmt = $conn->prepare("DELETE FROM Assets WHERE asset_id = ?");
    $stmt->execute([$id]);

    if ($stmt->rowCount() > 0) {
        echo json_encode(["status" => "success", "message" => "Aset berhasil dihapus"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Data tidak ditemukan atau sudah terhapus"]);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Gagal menghapus: " . $e->getMessage()]);
}
?>