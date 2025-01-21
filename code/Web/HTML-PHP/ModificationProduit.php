<?php
session_start();
// Vérifiez si l'utilisateur est connecté et a les droits nécessaires.
if (!isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}

$message = '';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    require_once 'Connect.inc.php';

    $numProduit = $_POST["numProduit"];

    // Vérifiez si le produit existe dans la base de données.
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE numProduit = ?");
    $stmt->execute([$numProduit]);
    $produit = $stmt->fetch();

    if ($produit) {
        // Produit trouvé, redirigez vers la page de modification avec le numProduit en session.
        $_SESSION['numProduit_to_modify'] = $numProduit;
        header("Location: ModificationProduitValidation.php");
        exit();
    } else {
        // Produit non trouvé, affichez un message d'erreur.
        $message = "Aucun produit trouvé avec le numéro spécifié.";
    }
}
?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Modification d'un Produit</title>
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
    max-width: 500px;
    margin-left: auto;
    margin-right: auto;
}

h2 {
    color: #333;
    margin-bottom: 20px;
}

form {
    display: flex;
    flex-direction: column;
}

label {
    display: block;
    margin-bottom: 5px;
    color: #666;
}

input[type="text"],
input[type="submit"] {
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box; /* Include padding and border in the element's width and height */
}

input[type="submit"] {
    background-color: #5cb85c;
    color: white;
    border: none;
    cursor: pointer;
    text-transform: uppercase;
    transition: background-color 0.3s ease;
}

input[type="submit"]:hover,
input[type="submit"]:focus {
    background-color: #4cae4c;
}

p {
    color: #cc0000;
}

    </style>
</head>
<body>
    <div class="container">
        <h2>Modifier un Produit</h2>
        <?php if ($message): ?>
            <p><?php echo $message; ?></p>
        <?php endif; ?>
        <form method="post">
            <label for="numProduit">Numéro du Produit à modifier :</label>
            <input type="text" id="numProduit" name="numProduit" required>
            <input type="submit" value="Rechercher Produit">
        </form>
    </div>
    <?php
include 'include/footer.php'; 
?>
</body>
</html>
