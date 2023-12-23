<?php
session_start();

if (!empty($_POST["connexion"]) && !empty($_POST["username"]) && !empty($_POST["password"])) {
    require_once 'Connect.inc.php';

    $username = $_POST["username"];
    $password = $_POST["password"];

    //R�cup�re le mot de passe du client voir si il correspond au username rentr�
    $stmt = $conn->prepare("SELECT mdpClient FROM Client WHERE pseudoClient = ?");
    $stmt->execute([$username]);
    $hashedPassword = $stmt->fetchColumn();
    //Regarde si le mot de passe crypt� correspond bien au mot de passe rentr�
    if ($hashedPassword && password_verify($password, $hashedPassword)) {
        //Cr�er la session
        $_SESSION['Sgroupe12'] = "oui";
        $_SESSION['nom'] = htmlentities($username);
        $reqId = $conn->prepare("SELECT numClient FROM Client WHERE pseudoClient = ?");
        $reqId->execute([$username]);
        $idClient = $reqId->fetchColumn();
        $_SESSION['numClient'] = $idClient;

        //Cr�er le cookie
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
