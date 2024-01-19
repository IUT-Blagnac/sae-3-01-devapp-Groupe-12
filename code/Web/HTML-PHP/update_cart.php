<?php
// Connexion à la base de données
require_once 'Connect.inc.php';

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    // Redirigez l'utilisateur vers la page de connexion si nécessaire
    echo json_encode(['success' => false, 'message' => 'Utilisateur non connecté']);
    exit;
}

if (isset($_SESSION['admin']) && $_SESSION['admin'] == "oui") {
    // Redirigez l'administrateur vers la page appropriée si nécessaire
    echo json_encode(['success' => false, 'message' => 'Accès refusé']);
    exit;
}

// Récupérez les données JSON de la requête POST
$data = json_decode(file_get_contents('php://input'), true);

if (!$data || !isset($data['numProduit']) || !isset($data['quantite'])) {
    // Vérifiez si les données JSON sont valides
    echo json_encode(['success' => false, 'message' => 'Données JSON invalides']);
    exit;
}

$numProduit = $data['numProduit'];
$newQuantite = $data['quantite'];

// Vous pouvez effectuer des validations supplémentaires ici, par exemple, vérifier si le produit existe, etc.

// Mettez à jour le panier en session
if (!isset($_SESSION['panier'])) {
    $_SESSION['panier'] = [];
}

$userID = $_SESSION['user_id'];

if (!isset($_SESSION['panier'][$userID])) {
    $_SESSION['panier'][$userID] = [];
}

// Mettez à jour la quantité du produit dans le panier
$found = false;
foreach ($_SESSION['panier'][$userID] as &$item) {
    if ($item['numProduit'] == $numProduit) {
        if ($newQuantite > 0) {
            $item['quantite'] = $newQuantite;
        } else {
            // Supprimez l'article du panier si la quantité est de 0
            unset($item);
        }
        $found = true;
        break;
    }
}

if (!$found && $newQuantite > 0) {
    // Si le produit n'est pas trouvé dans le panier, ajoutez-le avec la nouvelle quantité
    $_SESSION['panier'][$userID][] = ['numProduit' => $numProduit, 'quantite' => $newQuantite];
}

echo json_encode(['success' => true, 'message' => 'Mise à jour du panier réussie']);
?>
