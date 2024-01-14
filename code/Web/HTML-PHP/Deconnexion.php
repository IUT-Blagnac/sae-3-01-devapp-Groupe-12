<?php
session_start();
if (!isset($_SESSION['Sgroupe12']) && !isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}
session_destroy();
$message = "D�connexion effectu�e !";
header("Location: index.php?deconnexion_message=" . urlencode($message));
?>