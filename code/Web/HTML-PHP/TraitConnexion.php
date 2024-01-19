<?php
session_start();

if (!empty($_POST["connexion"]) && !empty($_POST["username"]) && !empty($_POST["password"])) {
    require_once 'Connect.inc.php';

    $username = $_POST["username"];
    $password = $_POST["password"];

    // Récupère le mot de passe et l'ID du client en fonction du pseudoClient
    $stmt = $conn->prepare("SELECT mdpClient, numClient FROM Client WHERE pseudoClient = ?");
    $stmt->execute([$username]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($result) {
        $hashedPassword = $result["mdpClient"];
        $idClient = $result["numClient"];

        if (password_verify($password, $hashedPassword)) {
            // Vérifie si l'utilisateur est un administrateur
            $stmtAdmin = $conn->prepare("SELECT numClient FROM admin WHERE numClient = ?");
            $stmtAdmin->execute([$idClient]);
            $isAdmin = $stmtAdmin->fetchColumn();

            if ($isAdmin) {
                $_SESSION['admin'] = "oui";
            } else {
                $_SESSION['user_id'] = $idClient; // Enregistrez l'identifiant unique de l'utilisateur
                $_SESSION['user_pseudo'] = $username; // Enregistrez le pseudoClient
            }

            if (isset($_POST['seSouvenirMoi'])) {
                setcookie("Cgroupe12", htmlentities($username), time() + 15 * 60, "/");
            }

            header('Location: index.php');
            exit();
        }
    }

    header('Location: FormConnexion.php?msgErreur=' . urlencode('Identifiants incorrects'));
    exit();
} else {
    header('Location: FormConnexion.php?msgErreur=' . urlencode('Veuillez remplir tous les champs'));
    exit();
}

