<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_POST['email'])) {
    require_once 'Connect.inc.php'; // Connexion à la base de données

    $email = $_POST['email'];

    // Vérifier si l'email existe dans la base de données
    $stmt = $pdo->prepare("SELECT numClient FROM Client WHERE mailClient = ?");
    $stmt->execute([$email]);
    $numClient = $stmt->fetchColumn();

    if ($numClient) {
        // Génération et stockage du token
        $token = bin2hex(random_bytes(16));
        $updateStmt = $pdo->prepare("UPDATE Client SET token = ? WHERE numClient = ?");
        $updateStmt->execute([$token, $numClient]);

        // Envoi de l'email avec le lien de réinitialisation
        $lienDeReinitialisation = "http://votresite.com/pageDeReinitialisation.php?token=$token";
        $sujet = "Réinitialisation de votre mot de passe";
        $message = "Pour réinitialiser votre mot de passe, cliquez sur ce lien : $lienDeReinitialisation";
        mail($email, $sujet, $message);

        // Planification de la suppression du token
        $suppressionStmt = $pdo->prepare("UPDATE Client SET token = NULL WHERE numClient = ? AND token = ?");
        $pdo->beginTransaction();
        $pdo->exec("SET EVENT_SCHEDULER = ON");
        $pdo->exec("CREATE EVENT IF NOT EXISTS effacerToken_$numClient 
                    ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 5 MINUTE 
                    DO
                    BEGIN
                        CALL :suppressionStmt;
                    END;");
        $pdo->commit();

        echo "Un email de réinitialisation a été envoyé.";
    } else {
        echo "Adresse email non trouvée.";
    }
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réinitialisation du mot de passe</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .reset-password-container {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            width: 300px;
        }
        .reset-password-container h2 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }
        .form-control {
            margin-bottom: 10px;
        }
        .form-control label {
            display: block;
            color: #666;
            margin-bottom: 5px;
        }
        .form-control input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box;
        }
        .form-control input[type="submit"] {
            background-color: #007bff;
            color: white;
            border: none;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .form-control input[type="submit"]:hover {
            background-color: #0056b3;
        }
        .message {
            text-align: center;
            margin-top: 20px;
        }
    </style>
</head>
<body>

<div class="reset-password-container">
    <h2>Réinitialiser le mot de passe</h2>
    <form action="MotDePasseOublie.php" method="post">
        <div class="form-control">
            <label for="email">Entrez votre adresse email :</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-control">
            <input type="submit" value="Réinitialiser le mot de passe">
        </div>
    </form>
</div>

</body>
</html>