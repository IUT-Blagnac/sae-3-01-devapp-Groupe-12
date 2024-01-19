<?php
session_start();
// Assurez-vous que l'utilisateur est connecté et qu'il a le droit de supprimer des produits
if (!isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    require_once 'Connect.inc.php';

    $numProduit = $_POST["numProduit"];
    $st = $conn->prepare("DELETE FROM ProduitApparente WHERE numProduitEnfant = ?");
    $st->execute([$numProduit]);

    $s = $conn->prepare("DELETE FROM LigneCde WHERE numProduit = ?");
    $s->execute([$numProduit]);
    // Préparez et exécutez la requête de suppression
    $stmt = $conn->prepare("DELETE FROM Produit WHERE numProduit = ?");
    $stmt->execute([$numProduit]);

    // Redirection ou message de confirmation
    echo "<script>alert('Le produit a été supprimé avec succès.');</script>";
    // header("Location: somepage.php"); // ou rediriger vers une autre page
}
?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Suppression d'un Produit</title>
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
        }

        label {
            display: block;
            margin-top: 10px;
            color: #666;
        }

        input[type="text"] {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box; /* Ajoutez cette propriété pour inclure le padding dans la largeur */
        }

        input[type="button"] {
            background-color: #5cb85c;
            color: white;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 4px;
            text-transform: uppercase;
        }

        input[type="button"]:hover {
            background-color: #4cae4c;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Supprimer un Produit</h2>
        <form id="deleteForm" action="SuppressionProduit.php" method="post">
            <label for="numProduit">Numéro du Produit :</label>
            <input type="text" id="numProduit" name="numProduit" required>
            <input type="button" value="Supprimer" onclick="confirmDelete();">
        </form>
    </div>

    <script>
    function confirmDelete() {
        var confirmAction = confirm("Êtes-vous sûr de vouloir supprimer ce produit ?");
        if (confirmAction) {
            // L'utilisateur a cliqué sur "OK", soumettre le formulaire
            document.getElementById("deleteForm").submit();
        } else {
            // L'utilisateur a cliqué sur "Annuler", ne rien faire
            console.log("Suppression annulée.");
        }
    }
    </script>
        <?php
include 'include/footer.php'; 
?>
</body>
</html>
