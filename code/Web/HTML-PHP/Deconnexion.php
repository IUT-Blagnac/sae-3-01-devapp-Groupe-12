<?php
session_start();
if (!isset($_SESSION['user_id']) && !isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}
unset($_SESSION['user_id']);
unset($_SESSION['pseudoClient']); // ou tout autre donnée spécifique à l'utilisateur
// Si vous utilisez des données spécifiques pour l'administrateur, supprimez-les également
unset($_SESSION['admin']);
$message = "D�connexion effectu�e !";
header("Location: index.php?deconnexion_message=" . urlencode($message));
