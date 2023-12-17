<?php
session_start();
require_once 'Connect.inc.php';

// check si l'utilisateur est connecté
if (!isset($_SESSION['Sgroupe12']) || $_SESSION['Sgroupe12'] !== "oui") {
    echo "Vous devez être connecté pour ajouter des articles au panier.";
    exit;
}

if (isset($_POST['numProduit'])) {
    $numProduit = $_POST['numProduit'];

    // Récupérer les détails du produit depuis la base de données
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE numProduit = ?");
    $stmt->execute([$numProduit]);
    $product = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$product) {
        echo "Produit non trouvé.";
        exit;
    }

    // Vérifiez si le cookie de panier existe déjà
    if (isset($_COOKIE['panier'])) {
        $cart = json_decode($_COOKIE['panier'], true);
    } else {
        $cart = [];
    }

    // Ajoutez le produit au panier ou augmentez la quantité
    $found = false;
    foreach ($cart as &$item) {
        if ($item['numProduit'] == $numProduit) {
            $item['quantite']++;
            $found = true;
            break;
        }
    }

    if (!$found) {
        $cart[] = ['numProduit' => $numProduit, 'quantite' => 1];
    }

    // Enregistrez le panier mis à jour dans un cookie
    setcookie('panier', json_encode($cart), time() + 86400 * 30, '/'); // Expiration dans 30 jours

    echo "Produit ajouté au panier avec succès.";
} else {
    echo "Aucun numéro de produit fourni.";
}
?>
