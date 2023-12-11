<?php
session_start();

if (!empty($_POST["connexion"]) && !empty($_POST["username"]) && !empty($_POST["password"])) {
    require_once 'Connect.inc.php';

    $username = $_POST["username"];
    $password = $_POST["password"];

    //Récupère le mot de passe du client voir si il correspond au username rentré
    $stmt = $conn->prepare("SELECT mdpClient FROM Client WHERE pseudoClient = ?");
    $stmt->execute([$username]);
    $hashedPassword = $stmt->fetchColumn();
    //Regarde si le mot de passe crypté correspond bien au mot de passe rentré
    if ($hashedPassword && password_verify($password, $hashedPassword)) {
        //Créer la session
        $_SESSION['Sgroupe12'] = "oui";
        $_SESSION['nom'] = htmlentities($username);
        
        //Créer le cookie
        if (isset($_POST['seSouvenirMoi'])) {
            setcookie("Cgroupe12", htmlentities($username), time() + 15 * 60, "/");
        }

        header('Location: index.php');
        exit();
    } else {
        header('Location: FormConnexion.php?msgErreur=' . urlencode('Identifiants incorrects'));
        exit();
    }
} else {
    header('Location: FormConnexion.php?msgErreur=' . urlencode('Veuillez remplir tous les champs'));
    exit();
}

?>