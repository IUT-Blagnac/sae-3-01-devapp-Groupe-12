<?php
require_once 'Connect.inc.php';

session_start();
if (!isset($_SESSION['Sgroupe12']) || $_SESSION['Sgroupe12'] != "oui") {
    header('Location: FormConnexion.php');
    exit();
}

$idClient = $_SESSION['numClient'];

// Mettre à jour les informations du client
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $stmt = $conn->prepare("UPDATE Client SET nomClient = ?, prenomClient = ?, adrRueClient = ?, adrCodePostalClient = ?, adrVilleClient = ?, adrPaysClient = ?, telephoneClient = ?, mailClient = ?, pseudoClient = ? WHERE numClient = ?");
    $stmt->execute([$_POST['nomClient'], $_POST['prenomClient'], $_POST['adrRueClient'], $_POST['adrCodePostalClient'], $_POST['adrVilleClient'], $_POST['adrPaysClient'], $_POST['telephoneClient'], $_POST['mailClient'], $_POST['pseudoClient'], $idClient]);

    // Rediriger vers VotreCompte.php pour rafraîchir les informations
    header('Location: VotreCompte.php');
    exit();
}

// Récupérer les informations du client pour les afficher
$stmt = $conn->prepare("SELECT * FROM Client WHERE numClient = ?");
$stmt->execute([$idClient]);
$client = $stmt->fetch(PDO::FETCH_ASSOC);
?>



<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon Compte</title>
    <style>
        .container {
    width: 80%;
    margin: 0 auto;
    padding: 20px;
}

.form-group {
    margin-bottom: 10px;
}

label {
    display: block;
    margin-bottom: 5px;
}

input[type="text"], input[type="email"], input[type="tel"] {
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

button {
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background-color: #45a049;
}

    </style>
</head>
<body>
    <?php include 'include/header.php'; ?>

    <div class="container">
        <h2>Mon Compte</h2>
        <form action="VotreCompte.php" method="post">
            <!-- Informations personnelles -->
            <div class="form-section">
                <h3>Informations Personnelles</h3>
                <div class="form-group">
                    <label for="nomClient">Nom:</label>
                    <input type="text" id="nomClient" name="nomClient" value="<?= htmlspecialchars($client['nomClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="prenomClient">Prénom:</label>
                    <input type="text" id="prenomClient" name="prenomClient" value="<?= htmlspecialchars($client['prenomClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="pseudoClient">Pseudo:</label>
                    <input type="text" id="pseudoClient" name="pseudoClient" value="<?= htmlspecialchars($client['pseudoClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="mailClient">Email:</label>
                    <input type="email" id="mailClient" name="mailClient" value="<?= htmlspecialchars($client['mailClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="telephoneClient">Téléphone:</label>
                    <input type="tel" id="telephoneClient" name="telephoneClient" value="<?= htmlspecialchars($client['telephoneClient']) ?>">
                </div>
            </div>

            <!-- Adresse -->
            <div class="form-section">
                <h3>Adresse</h3>
                <div class="form-group">
                    <label for="adrRueClient">Rue:</label>
                    <input type="text" id="adrRueClient" name="adrRueClient" value="<?= htmlspecialchars($client['adrRueClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="adrCodePostalClient">Code Postal:</label>
                    <input type="text" id="adrCodePostalClient" name="adrCodePostalClient" value="<?= htmlspecialchars($client['adrCodePostalClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="adrVilleClient">Ville:</label>
                    <input type="text" id="adrVilleClient" name="adrVilleClient" value="<?= htmlspecialchars($client['adrVilleClient']) ?>">
                </div>
                <div class="form-group">
                    <label for="adrPaysClient">Pays:</label>
                    <input type="text" id="adrPaysClient" name="adrPaysClient" value="<?= htmlspecialchars($client['adrPaysClient']) ?>">
                </div>
            </div>

            <!-- Autres informations -->
            <div class="form-section">
                <h3>Autres Informations</h3>
                <div class="form-group">
                    <label for="CA_cumule">Chiffre d'Affaires Cumulé:</label>
                    <input type="text" id="CA_cumule" name="CA_cumule" value="<?= htmlspecialchars($client['CA_cumule']) ?>" readonly>
                </div>
            </div>

            <button type="submit">Valider les changements</button>
        </form>
    </div>

    <?php include 'include/footer.php'; ?>
</body>
</html>
