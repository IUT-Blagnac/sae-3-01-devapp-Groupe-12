<?php
// Vérifiez si l'utilisateur est connecté
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['success' => false, 'message' => 'Utilisateur non connecté']);
    exit;
}

// Récupérez les données JSON de la requête POST
$data = json_decode(file_get_contents('php://input'), true);

if (!$data || !isset($data['numProduit'])) {
    echo json_encode(['success' => false, 'message' => 'Données JSON invalides']);
    exit;
}

$numProduit = $data['numProduit'];

// Vérifiez si le produit existe dans le panier de l'utilisateur
if (isset($_SESSION['panier'][$_SESSION['user_id']])) {
    $cart = $_SESSION['panier'][$_SESSION['user_id']];
    $updatedCart = [];

    foreach ($cart as $item) {
        if ($item['numProduit'] != $numProduit) {
            $updatedCart[] = $item;
        }
    }

    // Mettez à jour le panier de l'utilisateur
    $_SESSION['panier'][$_SESSION['user_id']] = $updatedCart;

    echo json_encode(['success' => true, 'message' => 'Suppression de l\'article du panier réussie']);
} else {
    echo json_encode(['success' => false, 'message' => 'Panier introuvable']);
}
?>
