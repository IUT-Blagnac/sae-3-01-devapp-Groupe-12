<?php
require_once 'Connect.inc.php';

session_start();
if (!isset($_SESSION['user_id'])) {
    // Rediriger l'utilisateur s'il n'est pas connecté
    header('Location: FormConnexion.php');
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $numProduit = $_POST['numProduit'] ?? null;
    $numClient = $_SESSION['numClient'];
    $note = $_POST['note'] ?? null;
    $commentaire = $_POST['commentaire'] ?? '';

    // Vérifier si l'utilisateur a déjà laissé un avis pour ce produit
    $stmt = $conn->prepare("SELECT COUNT(*) FROM Avis WHERE numProduit = ? AND numClient = ?");
    $stmt->execute([$numProduit, $numClient]);
    $hasReviewed = $stmt->fetchColumn() > 0;

    if ($hasReviewed) {
        // Rediriger vers la page du produit avec un message d'erreur
        header("Location: detailsProduit.php?numProduit=$numProduit&error=alreadyReviewed");
        exit;
    }

    // Insérer l'avis dans la base de données
    $stmt = $conn->prepare("INSERT INTO Avis (numProduit, numClient, note, commentaire) VALUES (?, ?, ?, ?)");
    $stmt->execute([$numProduit, $numClient, $note, $commentaire]);

    // Rediriger vers la page du produit
    header("Location: detailsProduit.php?numProduit=$numProduit&success=reviewAdded");
    exit();
}
?>
