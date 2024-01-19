<?php
session_start();
// Vérification de la méthode de requête
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Connexion à la base de données
    require_once 'Connect.inc.php';

    // Vérification de l'existence des champs requis
    if (isset($_POST['prenom']) && isset($_POST['nom']) && isset($_POST['pseudo']) && isset($_POST['email']) && isset($_POST['telephone'])
        && isset($_POST['age']) && isset($_POST['password']) && isset($_POST['confirm-password'])) {

        // Récupération et nettoyage des données du formulaire
        $prenomClient = trim($_POST['prenom']);
        $nomClient = trim($_POST['nom']);
        $pseudoClient = trim($_POST['pseudo']);
        $emailClient = trim($_POST['email']);
        $telClient = trim($_POST['telephone']);
        $ageClient = $_POST['age'];
        $passwordClient = $_POST['password'];
        $passwordConfirmClient = $_POST['confirm-password'];

        // Vérification des contraintes

        // Nom et prénom sans chiffres
        if (!ctype_alpha(str_replace(' ', '', $prenomClient)) || !ctype_alpha(str_replace(' ', '', $nomClient))) {
            header('Location: Inscription.php?msgErreur=Nom ou prénom invalide');
            exit();
        }

        // Adresse email valide
        if (!filter_var($emailClient, FILTER_VALIDATE_EMAIL)) {
            header('Location: Inscription.php?msgErreur=Adresse email invalide');
            exit();
        }

        // Format du numéro de téléphone
        if (!preg_match('/^[0-9]{10}$/', $telClient)) {
            header('Location: Inscription.php?msgErreur=Numéro de téléphone invalide');
            exit();
        }

        // Vérification de l'âge (plus de 18 ans)
        $dateNaissance = new DateTime($ageClient);
        $aujourdhui = new DateTime('today');
        $age = $dateNaissance->diff($aujourdhui)->y;
        if ($age < 18) {
            header('Location: Inscription.php?msgErreur=Vous devez avoir plus de 18 ans');
            exit();
        }

        // Correspondance des mots de passe
        if ($passwordClient != $passwordConfirmClient) {
            header('Location: Inscription.php?msgErreur=Les mots de passe ne correspondent pas');
            exit();
        }

        // Unicité du pseudo
        $stmt = $conn->prepare("SELECT COUNT(*) FROM Client WHERE pseudoClient = ?");
        $stmt->execute([$pseudoClient]);
        if ($stmt->fetchColumn() > 0) {
            header('Location: Inscription.php?msgErreur=Le pseudo est déjà utilisé');
            exit();
        }

        // Hashage du mot de passe
        $passwordClientHashed = password_hash($passwordClient, PASSWORD_DEFAULT);

        try {
            // Insertion du nouveau client dans la base de données
            $stmt = $conn->prepare("INSERT INTO Client (prenomClient, nomClient, pseudoClient, mailClient, telephoneClient, mdpClient) VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->execute([$prenomClient, $nomClient, $pseudoClient, $emailClient, $telClient, $passwordClientHashed]);

            // Récupération de l'identifiant du dernier utilisateur inséré
            $idClient = $conn->lastInsertId();

            //Création de la session
            $_SESSION['user_id'] = $idClient; // Enregistrez l'identifiant unique de l'utilisateur
            $_SESSION['user_pseudo'] = $pseudoClient; // Enregistrez le pseudoClient
            
            header('Location: index.php'); // Rediriger vers une page d'accueil
            exit();
        } catch (PDOException $e) {
            // Gestion des erreurs
            $errorMsg = urlencode($e->getMessage());
            header("Location: Inscription.php?msgErreur=Erreur lors de l'enregistrement: $errorMsg");
            exit();
        }
    } else {
        // Si certains champs sont manquants
        header('Location: Inscription.php?msgErreur=Veuillez remplir tous les champs du formulaire');
        exit();
    }
} else {
    // Si la méthode de requête n'est pas POST
    header('Location: Inscription.php');
    exit();
}
?>
