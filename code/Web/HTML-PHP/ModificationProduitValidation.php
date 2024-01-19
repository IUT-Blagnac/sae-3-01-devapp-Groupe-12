<?php
session_start();
require_once 'Connect.inc.php';

// Assurez-vous que l'utilisateur est connecté en tant qu'admin
if (!isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}

$message = '';
$numProduit = $_SESSION['numProduit_to_modify'] ?? null;

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Construire la requête SQL dynamiquement en fonction des champs remplis
    $updates = [];
    $params = [];
    
    foreach ($_POST as $key => $value) {
        if ($value != '' && $key != 'numProduit') { // Ignorer les champs vides et le numéro de produit
            $updates[] = "$key = ?";
            $params[] = $value;
        }
    }

    if (count($updates) > 0) {
        $params[] = $numProduit;
        $sql = "UPDATE Produit SET " . implode(', ', $updates) . " WHERE numProduit = ?";
        $stmt = $conn->prepare($sql);
        
        if ($stmt->execute($params)) {
            $message = "Le produit a été modifié avec succès.";
            header("Location: ModificationProduit.php");
            exit();

        } else {
            $message = "Erreur lors de la mise à jour du produit.";
        }
    } else {
        $message = "Aucune modification fournie.";
    }
}

// Récupérer les informations actuelles du produit pour les afficher dans le formulaire
if ($numProduit) {
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE numProduit = ?");
    $stmt->execute([$numProduit]);
    $produit = $stmt->fetch(PDO::FETCH_ASSOC);
}

if (!$produit) {
    $message = "Produit avec le numéro $numProduit introuvable.";
    unset($_SESSION['numProduit_to_modify']); // Nettoyer la session
    header("Location: ModificationProduit.php");
    exit();
}


?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Validation de Modification d'un Produit</title>
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
        }

        form {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        form label {
            font-weight: bold;
        }

        form input[type="text"],
        form select,
        form textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 10px;
        }

        form textarea {
            height: 100px; /* Adjust the height as needed */
        }

        form input[type="submit"] {
            grid-column: span 2;
            padding: 10px 0;
            background-color: #5cb85c;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-transform: uppercase;
            transition: background-color 0.3s ease;
        }

        form input[type="submit"]:hover {
            background-color: #4cae4c;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Modification du Produit #<?php echo htmlspecialchars($numProduit); ?></h2>
        <form method="post">
            <label for="coutAchat">Coût d'achat :</label>
            <input type="text" id="coutAchat" name="coutAchat" value="<?php echo htmlspecialchars($produit['coutAchat'] ?? ''); ?>">

            <label for="prixVente">Prix de vente :</label>
            <input type="text" id="prixVente" name="prixVente" value="<?php echo htmlspecialchars($produit['prixVente'] ?? ''); ?>">

            <label for="codeBarre">Code Barre :</label>
            <input type="text" id="codeBarre" name="codeBarre" value="<?php echo htmlspecialchars($produit['codeBarre'] ?? ''); ?>">
            <label for="stock">Stock :</label>
        <input type="text" id="stock" name="stock" value="<?php echo htmlspecialchars($produit['stock'] ?? ''); ?>">

        <label for="description">Description :</label>
        <textarea id="description" name="description"><?php echo htmlspecialchars($produit['description'] ?? ''); ?></textarea>

        <!-- Champ caché pour maintenir l'ID du produit lors de la soumission du formulaire -->
        <input type="hidden" name="numProduit" value="<?php echo htmlspecialchars($numProduit); ?>">

        <input type="submit" value="Enregistrer les modifications">
    </form>
</div>
<?php
include 'include/footer.php'; 
?>
</body>
</html>