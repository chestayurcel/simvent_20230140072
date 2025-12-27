<?php
// assets/read.php
require_once '../config/database.php';
require_once '../utils/auth_helper.php';

authenticate($conn);

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    
    // --- 1. LOGIKA PAGINATION ---
    $limit = 20; // Maksimal 20 data per request
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $offset = ($page - 1) * $limit;

    // --- 2. QUERY DASAR ---
    $sql = "SELECT a.*, r.room_name 
            FROM Assets a 
            JOIN Rooms r ON a.room_id = r.room_id 
            WHERE 1=1";
    
    $params = [];

    // Filter Ruangan
    if (!empty($_GET['room_id'])) {
        $sql .= " AND a.room_id = ?";
        $params[] = $_GET['room_id'];
    }

    // Pencarian
    if (!empty($_GET['search'])) {
        $sql .= " AND a.asset_name LIKE ?";
        $params[] = "%" . $_GET['search'] . "%";
    }

    // --- 3. TAMBAHKAN LIMIT (Wajib di akhir) ---
    $sql .= " ORDER BY a.asset_id DESC LIMIT $limit OFFSET $offset";

    try {
        $stmt = $conn->prepare($sql);
        $stmt->execute($params);
        $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Return JSON
        echo json_encode([
            "status" => "success", 
            "page" => $page,
            "data" => $data
        ]);

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
}
?>