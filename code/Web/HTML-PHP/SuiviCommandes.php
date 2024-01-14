<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
require_once 'Connect.inc.php';
if (!isset($_SESSION['Sgroupe12']) || $_SESSION['Sgroupe12'] != "oui") {
    echo "<script>
    alert(\"Vous devez être connecté pour voir vos commandes. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}


$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>Mes commandes</title>
    <link rel="stylesheet" href="Css/SuiviCommandes.css">
</head>

<body>
    <?php
    include 'include/header.php';

    $sql = "SELECT * FROM Commande WHERE numClient = :numClient";
    $stmt = $conn->prepare($sql);
    $stmt->execute(['numClient' => $_SESSION['numClient']]);
    $result = $stmt->fetchAll();

    if (count($result) > 0) {
        echo '<div class="center">';
        echo "<table class='table'>";
        echo "<thead>";
        echo "<tr>";
        echo "<th>Commande n°</th>";
        echo "<th>Date de commande</th>";
        echo "<th>Montant total</th>";
        echo "<th>Statut</th>";
        echo "<th>Détails des produits</th>";
        echo "</tr>";
        echo "</thead>";
        echo "<tbody>";

        foreach ($result as $row) {
            echo "<tr>";
            echo "<td>" . $row["numCommande"] . "</td>";

            // Formater la date au format français
            $dateCommande = date("d/m/Y", strtotime($row["dateCommande"]));
            echo "<td>" . $dateCommande . "</td>";

            // Formater le montant avec le symbole d'Euro
            $montant = number_format($row["montant"], 2, ',', ' ') . " €";
            echo "<td>" . $montant . "</td>";

            echo "<td>" . $row["Statut"] . "</td>";

            // Ajouter la section pour les détails des produits (initialement masquée)
            echo "<td>";
            // Récupérer les détails des produits de la table LigneCde pour cette commande
            $numCommande = $row["numCommande"];
            $sqlDetails = "SELECT * FROM LigneCde WHERE numCommande = $numCommande";
            $detailsReq = $conn->prepare($sqlDetails);
            $detailsReq->execute();
            $resultDetails = $detailsReq->fetchAll();

            if (count($resultDetails) > 0) {
                
                echo "<table class='details-produits'>";
                echo "<thead>";
                echo "<tr>";
                echo "<th>Produit n°</th>";
                echo "<th>Quantité commandée</th>";
                echo "</tr>";
                echo "</thead>";
                echo "<tbody>";

                foreach ($resultDetails as $details) {
                    echo "<tr>";
                    echo "<td>" . $details["numProduit"] . "</td>";
                    echo "<td>" . $details["quantiteCommandee"] . "</td>";

                    echo "<td><a href='detailsProduit.php?numProduit=" . $details["numProduit"] . "' class='button-details'>Voir le produit</a></td>";
                    echo "</tr>";
                }

                echo "</tbody>";
                echo "</table>";
            } else {
                echo "<p>Aucun détail de produit trouvé.</p>";
            }
            echo "</td>"; 

            echo "</tr>";
        }

        echo "</tbody>";
        echo "</table>";
    } else {
        echo "<h2>Aucune commande trouvée.</h2>";
    }
    echo '</div>';
    ?>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $('.category-select').on('change', function() {
                var selectedCategory = $(this).val();
                var selectedCategoryText = $(this).find('option:selected').text();

                $('#search-form').submit();
            });

            $('#search-form').on('submit', function(e) {
                e.preventDefault();

                var recherche = $('input[name="search"]').val().trim();
                var selectedCategory = $('select[name="category"]').val();
                var queryParams = $.param({
                    search: recherche,
                    category: selectedCategory
                });
                window.location.href = 'index.php?' + queryParams;
            });
        });
    </script>
    <?php
    include 'include/footer.php';
    ?>
</body>

</html>
<!DOCTYPE html>
<html lang="fr">