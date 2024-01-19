<?php
require_once 'Connect.inc.php';
session_start();
if (!isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}

// Récupérer toutes les commandes
$stmt = $conn->query("SELECT * FROM Commande");
$commandes = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ventes</title>
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
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
}

h2 {
    color: #333;
    margin-bottom: 20px;
    text-align: center;
}

.commande {
    border: 1px solid #ddd;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 4px;
}

h3 {
    color: #444;
    margin-bottom: 10px;
}

.produit {
    padding-left: 20px;
    border-left: 2px solid #eee;
    margin-top: 10px;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

th {
    background-color: #f8f8f8;
}

tr:nth-child(even) {
    background-color: #f2f2f2;
}

    </style>
</head>
<body>
    <div class="container">
        <h2>Liste des Ventes</h2>
        <?php foreach ($commandes as $commande): ?>
            <div class="commande">
                <h3>Commande #<?php echo htmlspecialchars($commande['numCommande']); ?></h3>
                <p>Date de commande: <?php echo htmlspecialchars($commande['dateCommande']); ?></p>
                <p>Montant Frais: <?php echo htmlspecialchars($commande['montantFrais']); ?></p>
                <p>Montant Total: <?php echo htmlspecialchars($commande['montant']); ?></p>
                <p>Numéro Client: <?php echo htmlspecialchars($commande['numClient']); ?></p>
                <h4>Produits commandés:</h4>
            <ul>
                <?php
                // Récupérer les détails des produits pour chaque commande
                $stmt = $conn->prepare("SELECT * FROM LigneCde WHERE numCommande = ?");
                $stmt->execute([$commande['numCommande']]);
                $ligneCdes = $stmt->fetchAll(PDO::FETCH_ASSOC);

                foreach ($ligneCdes as $ligneCde) {
                    echo "<li>";
                    echo "Produit #".$ligneCde['numProduit'].", Quantité: ".$ligneCde['quantiteCommandee'];
                    echo "</li>";
                }
                ?>
            </ul>
        </div>
    <?php endforeach; ?>
</div>
<?php
include 'include/footer.php'; 
?>
</body>
</html>