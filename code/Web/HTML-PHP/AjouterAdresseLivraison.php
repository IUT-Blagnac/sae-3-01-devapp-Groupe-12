<?php

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    echo "<script>
    alert(\"Vous devez être connecté pour ajouter une adresse de livraison. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}

$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
$msgErreur = "";
if (isset($_POST['valider'])) {
    // Récupération des valeurs du formulaire
    $adrLivraison = $_POST['adrLivraison'];
    $adrPostaleLivraison = $_POST['adrPostaleLivraison'];
    $adrVilleLivraison = $_POST['adrVilleLivraison'];
    $adrPaysLivraison = $_POST['adrPaysLivraison'];
    $LibelleLivraison = $_POST['LibelleLivraison'];
    $numClient = $_SESSION['numClient'];

    // Définition des expressions régulières pour la validation
    $regexAdresse = '/^[a-zA-Z0-9\s\-\']+$/';
    $regexCodePostal = '/^\d{5}$/';
    $regexVille = '/^[a-zA-Z\s\-\']+$/';
    $regexPays = '/^[a-zA-Z\s\-\']+$/';
    $regexLibelle = '/^[a-zA-Z0-9\s\-\']+$/';

    // Validation des champs
    $errorMessages = array();

    if (!preg_match($regexAdresse, $adrLivraison)) {
        $errorMessages['adrLivraison'] = "Adresse de livraison invalide.";
    }

    if (!preg_match($regexCodePostal, $adrPostaleLivraison)) {
        $errorMessages['adrPostaleLivraison'] = "Code postale invalide.";
    }

    if (!preg_match($regexVille, $adrVilleLivraison)) {
        $errorMessages['adrVilleLivraison'] = "Ville invalide.";
    }

    if (!preg_match($regexPays, $adrPaysLivraison)) {
        $errorMessages['adrPaysLivraison'] = "Pays invalide.";
    }

    if (!preg_match($regexLibelle, $LibelleLivraison)) {
        $errorMessages['LibelleLivraison'] = "Libellé invalide.";
    }

    if (
        preg_match($regexAdresse, $adrLivraison) &&
        preg_match($regexCodePostal, $adrPostaleLivraison) &&
        preg_match($regexVille, $adrVilleLivraison) &&
        preg_match($regexPays, $adrPaysLivraison) &&
        preg_match($regexLibelle, $LibelleLivraison)
    ) {
        // Requête SQL d'insertion
        require_once('Connect.inc.php');
        try {
            $req = $conn->prepare("INSERT INTO AdresseLivraison (adrPostaleLivraison, adrVilleLivraison, adrPaysLivraison, adrLivraison, numClient, LibelleLivraison)
                VALUES (:adrPostaleLivraison, :adrVilleLivraison, :adrPaysLivraison, :adrLivraison, :numClient, :LibelleLivraison)");
            $req->bindParam(':adrPostaleLivraison', $adrPostaleLivraison);
            $req->bindParam(':adrVilleLivraison', $adrVilleLivraison);
            $req->bindParam(':adrPaysLivraison', $adrPaysLivraison);
            $req->bindParam(':adrLivraison', $adrLivraison);
            $req->bindParam(':numClient', $numClient);
            $req->bindParam(':LibelleLivraison', $LibelleLivraison);
            $req->execute();
            echo '<script>alert("Nouvelle adresse de livraison ajoutée avec succès!");</script>';
            unset($_POST);
        } catch (\PDOException $th) {
            echo '<script>alert("Une erreur s\'est produite, veuillez réessayer !\nCode d\'erreur : ' . $th->getMessage() . '");</script>';
        }
    }
}


?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>Service Client</title>
    <link rel="stylesheet" href="Css/AjouterAdresseLivraison.css">
</head>

<body>
    <?php include 'include/header.php'; ?>

    <br><br><br>
    <div class="center">
        <form method="POST" class="adresse-form">
            <div class="form-group">
                <label for="adrLivraison">Adresse :</label>
                <input type="text" name="adrLivraison" class="input-field <?= isset($errorMessages['adrLivraison']) ? 'error-input' : ''; ?>" value="<?= isset($_POST['adrLivraison']) ? htmlspecialchars($_POST['adrLivraison']) : ''; ?>">
                <div class="error-message"><?= isset($errorMessages['adrLivraison']) ? $errorMessages['adrLivraison'] : ''; ?></div>
            </div>
            <div class="form-group">
                <label for="adrPostaleLivraison">Code postale :</label>
                <input type="text" name="adrPostaleLivraison" class="input-field <?= isset($errorMessages['adrPostaleLivraison']) ? 'error-input' : ''; ?>" value="<?= isset($_POST['adrPostaleLivraison']) ? htmlspecialchars($_POST['adrPostaleLivraison']) : ''; ?>">
                <div class="error-message"><?= isset($errorMessages['adrPostaleLivraison']) ? $errorMessages['adrPostaleLivraison'] : ''; ?></div>
            </div>
            <div class="form-group">
                <label for="adrVilleLivraison">Ville :</label>
                <input type="text" name="adrVilleLivraison" class="input-field <?= isset($errorMessages['adrVilleLivraison']) ? 'error-input' : ''; ?>" value="<?= isset($_POST['adrVilleLivraison']) ? htmlspecialchars($_POST['adrVilleLivraison']) : ''; ?>">
                <div class="error-message"><?= isset($errorMessages['adrVilleLivraison']) ? $errorMessages['adrVilleLivraison'] : ''; ?></div>
            </div>
            <div class="form-group">
                <label for="adrPaysLivraison">Pays :</label>
                <input type="text" name="adrPaysLivraison" class="input-field <?= isset($errorMessages['adrPaysLivraison']) ? 'error-input' : ''; ?>" value="<?= isset($_POST['adrPaysLivraison']) ? htmlspecialchars($_POST['adrPaysLivraison']) : ''; ?>">
                <div class="error-message"><?= isset($errorMessages['adrPaysLivraison']) ? $errorMessages['adrPaysLivraison'] : ''; ?></div>
            </div>
            <div class="form-group">
                <label for="LibelleLivraison">Libellé de l'adresse :</label>
                <input type="text" name="LibelleLivraison" class="input-field <?= isset($errorMessages['LibelleLivraison']) ? 'error-input' : ''; ?>" value="<?= isset($_POST['LibelleLivraison']) ? htmlspecialchars($_POST['LibelleLivraison']) : ''; ?>">
                <div class="error-message"><?= isset($errorMessages['LibelleLivraison']) ? $errorMessages['LibelleLivraison'] : ''; ?></div>
            </div>
            <div class="right">
                <input type="submit" value="Ajouter" name="valider" class="AjouterButt">
            </div>
        </form>
        <br><br>
    </div>
    <div class="left">
        <a class="choix-boutton" href="CommanderChoixLivraison.php">Retour au choix de livraison</a>
    </div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#livraison-domicile-btn').click(function() {
                $('.adresse-livraison').toggle();
            });
        });
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
    <?php include 'include/footer.php'; ?>
</body>

</html>
<!DOCTYPE html>
<html lang="fr">