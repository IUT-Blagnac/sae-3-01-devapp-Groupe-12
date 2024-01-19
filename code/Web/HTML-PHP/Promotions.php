<?php
session_start
();
require_once 'Connect.inc.php';

// Assurez-vous que l'utilisateur est connecté en tant qu'admin
if (!isset($_SESSION['admin'])) {
header("Location: index.php");
exit();
}

$message = '';

// Lorsque le formulaire est soumis
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['promotion']) && isset($_POST['produits'])) {
$promotion = (float) $_POST['promotion'];
$produitsSelectionnes = $_POST['produits'];
// Vérifiez si le pourcentage de promotion est valide
if ($promotion <= 0 || $promotion > 100) {
    $message = "Pourcentage de promotion invalide.";
} else {
    foreach ($produitsSelectionnes as $numProduit) {
        $stmt = $conn->prepare("UPDATE Produit SET prixVente = prixVente * (1 - ? / 100) WHERE numProduit = ?");
        $stmt->execute([$promotion, $numProduit]);
    }
    $message = "Promotion appliquée sur les produits sélectionnés.";
}
}

// Récupérer tous les produits
$stmt = $conn->query("SELECT numProduit, nomProduit FROM Produit");
$produits = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Promotions</title>
    <style>
       body {
    font-family: Arial, sans-serif;
    background-color: #f4f4f4;
    padding: 10px;
}

.container {
    background-color: #fff;
    padding: 20px;
    margin-top: 50px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
}

h2 {
    color: #333;
    margin-bottom: 20px;
    text-align: center;
}

.liste-produits {
    margin-bottom: 20px;
}

.liste-produits div {
    border-bottom: 1px solid #eee;
    padding: 10px;
}

label {
    display: block;
    margin-bottom: 5px;
}

input[type="number"],
input[type="submit"] {
    width: 100%;
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
}

input[type="submit"] {
    background-color:
#4CAF50;
color: white;
border: none;
cursor: pointer;
text-transform: uppercase;
transition: background-color 0.3s ease;
}

input[type="submit"]:hover {
background-color: #45a049;
}

p {
color: #cc0000;
margin-bottom: 20px;
}
    </style>
</head>
<body>
    <div class="container">
        <h2>Promotions sur les Produits</h2>
        <?php if ($message): ?>
            <p><?php echo $message; ?></p>
        <?php endif; ?>
        <form method="post">
        <div class="liste-produits">
            <?php foreach ($produits as $produit): ?>
                <div>
                    <input type="checkbox" name="produits[]" value="<?php echo htmlspecialchars($produit['numProduit']); ?>">
                    <?php echo htmlspecialchars($produit['nomProduit']); ?>
                </div>
            <?php endforeach; ?>
        </div>
        <label for="promotion">Pourcentage de promotion (%):</label>
        <input type="number" id="promotion" name="promotion" min="0" max="100" required>

        <input type="submit" value="Lancer la promotion">
    </form>
</div>
<?php
include 'include/footer.php'; 
?>
</body>
</html>

