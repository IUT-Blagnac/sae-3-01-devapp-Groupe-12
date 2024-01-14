<?php
session_start();

if (!empty($_POST["connexion"]) && !empty($_POST["username"]) && !empty($_POST["password"])) {
    require_once 'Connect.inc.php';

    $username = $_POST["username"];
    $password = $_POST["password"];

    // Récupère le mot de passe du client pour voir s'il correspond au nom d'utilisateur entré
    $stmt = $conn->prepare("SELECT mdpClient, numClient FROM Client WHERE pseudoClient = ?");
    $stmt->execute([$username]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($result) {
        $hashedPassword = $result["mdpClient"];
        $idClient = $result["numClient"];

        // Regarde si le mot de passe crypté correspond bien au mot de passe entré
        if ($hashedPassword && password_verify($password, $hashedPassword)) {
            // Vérifie si l'utilisateur est un administrateur
            $stmtAdmin = $conn->prepare("SELECT numClient FROM admin WHERE numClient = ?");
            $stmtAdmin->execute([$idClient]);
            $isAdmin = $stmtAdmin->fetchColumn();

            if ($isAdmin) {
                // L'utilisateur est un admin
                $_SESSION['admin'] = "oui";
            } else {
                // L'utilisateur n'est pas un admin, utilise la session "Sgroupe12"
                $_SESSION['Sgroupe12'] = "oui";
            }
            $_SESSION['nom'] = htmlentities($username);
            $_SESSION['numClient'] = $idClient;

            // Créer le cookie
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

