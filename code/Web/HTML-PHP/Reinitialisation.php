
Pour modifier le script traitementReinitialisation.php afin de vérifier le token et de mettre à jour le mot de passe dans la base de données, suivez ces étapes :

Vérifier le token : Assurez-vous que le token fourni correspond à celui stocké dans la base de données pour l'utilisateur concerné.
Mettre à jour le mot de passe : Si les mots de passe saisis correspondent et que le token est valide, mettez à jour le mot de passe dans la base de données.
Voici un exemple de code modifié pour traitementReinitialisation.php :

php
Copy code
<?php
session_start();

if ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST['password']) && !empty($_POST['confirm-password']) && !empty($_POST['token'])) {
    require_once 'Connect.inc.php'; // Connexion à la base de données

    $password = $_POST['password'];
    $confirmPassword = $_POST['confirm-password'];
    $token = $_POST['token'];

    if ($password === $confirmPassword) {
        // Vérifier si le token est valide
        $stmt = $pdo->prepare("SELECT numClient FROM Client WHERE token = ?");
        $stmt->execute([$token]);
        $numClient = $stmt->fetchColumn();

        if ($numClient) {
            // Mettre à jour le mot de passe dans la base de données
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
            $updateStmt = $pdo->prepare("UPDATE Client SET mdpClient = ?, token = NULL WHERE numClient = ?");
            $updateStmt->execute([$hashedPassword, $numClient]);

            // Redirection vers la page de connexion
            header('Location: connexion.php');
            exit();
        } else {
            echo "Token invalide.";
        }
    } else {
        echo "Les mots de passe ne correspondent pas.";
    }
} else {
    echo "Veuillez remplir tous les champs.";
}
?>



<!DOCTYPE html>
<html>
<head>
    <title>Réinitialisation du mot de passe</title>
    <!-- Styles et métadonnées -->
</head>
<body>
    <form action="Reinitialisation.php" method="post">
        <input type="hidden" name="token" value="<?php echo $_GET['token']; ?>">
        <label for="password">Nouveau mot de passe:</label>
        <input type="password" id="password" name="password" required>
        <label for="confirm-password">Confirmer le mot de passe:</label>
        <input type="password" id="confirm-password" name="confirm-password" required>
        <input type="submit" value="Modifier">
    </form>
</body>
</html>
