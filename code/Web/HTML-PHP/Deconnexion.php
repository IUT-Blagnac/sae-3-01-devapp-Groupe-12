<?php
session_start();
if (!isset($_SESSION['Sgroupe12'])) {
    header("Location: index.php");
    exit();
}
session_destroy();
$message = "Déconnexion effectuée !";
header("Location: index.php?deconnexion_message=" . urlencode($message));
?>