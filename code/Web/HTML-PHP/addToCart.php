<?php
session_start();
require_once 'Connect.inc.php';

// Vérifier si l'utilisateur est connecté
if (!isset($_SESSION['user_id'])) {
    echo "<script>
    alert(\"Vous devez être connecté pour ajouter des articles au Panier. Vous allez être redirigé vers la page de connexion.\");
    window.location.href = 'FormConnexion.php'</script>";
    exit;
}

if (isset($_SESSION['admin']) && $_SESSION['admin'] == "oui") {
    echo "<script>
    alert(\"Vous devez être connecté en tant que client pour ajouter des articles au Panier. Vous allez être redirigé vers la page de connexion.\");
    window.location.href = 'FormConnexion.php'</script>";
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

    // Initialiser le panier pour l'utilisateur actuel
    if (!isset($_SESSION['panier'][$_SESSION['user_id']])) {
        $_SESSION['panier'][$_SESSION['user_id']] = [];
    }

    $cart = &$_SESSION['panier'][$_SESSION['user_id']];

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

    echo "Produit ajouté au panier avec succès.";
} else {
    echo "Aucun numéro de produit fourni.";
}

