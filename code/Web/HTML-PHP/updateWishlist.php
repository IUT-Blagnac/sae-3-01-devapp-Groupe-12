<?php
session_start();
require_once 'Connect.inc.php';

if (!isset($_SESSION['user_id'])) {
    echo "Utilisateur non connecté";
    exit;
}

if (isset($_POST['numProduit'])) {
    $numProduit = $_POST['numProduit'];

    if (!isset($_SESSION['wishlist'][$_SESSION['user_id']])) {
        $_SESSION['wishlist'][$_SESSION['user_id']] = [];
    }

    $wishlist = &$_SESSION['wishlist'][$_SESSION['user_id']];

    $key = array_search($numProduit, $wishlist);
    if ($key !== false) {
        unset($wishlist[$key]);
        echo "Produit retiré de la wishlist";
    } else {
        $wishlist[] = $numProduit;
        echo "Produit ajouté à la wishlist";
    }
} else {
    echo "Aucun numéro de produit fourni";
}
?>
