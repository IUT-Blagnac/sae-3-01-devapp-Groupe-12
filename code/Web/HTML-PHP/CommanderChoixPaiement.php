<?php

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['Sgroupe12']) || $_SESSION['Sgroupe12'] != "oui") {
    echo "<script>
    alert(\"Vous devez être connecté passer une commande. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}

if (isset($_COOKIE['panier'])) {
    $cart = json_decode($_COOKIE['panier'], true);
} else {
    echo "<script>
    alert(\"Votre panier ne doit pas être vide pour passer une commande. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}
if (count($cart) == 0) {
    echo "<script>
    alert(\"Votre panier ne doit pas être vide pour passer une commande. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}

if (!isset($_SESSION['choixLivraison'])) {
    echo "<script>
    alert(\"Merci de passer par le choix de la livraison avant de passer au paiement.\");
    window.location.href = 'CommanderChoixLivraison.php'</script>";
}

$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';

$msgErreur = "";
$choixPaiement = "";
$infosPaiement = [];

if (isset($_POST['retourPanier'])) {
    unset($_SESSION['choixLivraison']);
    unset($_SESSION['choixPaiement']);
    header("Location: Panier.php");
    exit();
} else if (isset($_POST['autresOptions'])) {
    unset($_SESSION['choixPaiement']);
    header("Location: CommanderChoixPaiement.php");
    exit();
} else if (isset($_POST['retour'])) {
    unset($_SESSION['choixPaiement']);
    header("Location: CommanderChoixLivraison.php");
    exit();
} else if (isset($_POST['carteBancaire'])) {
    $choixPaiement = "carteBancaire";
} else if (isset($_POST['paypal'])) {
    $choixPaiement = "paypal";
} else if (isset($_POST['magasin'])) {
    $infosPaiement['methode'] = "Paiement en magasin";
    $_SESSION['choixPaiement'] = $infosPaiement;
    header("Location: CommanderConfirmation.php");
    exit();
} else {
    if (isset($_POST['validerCarteBancaire'])) {
        $infosPaiement['methode'] = "Carte bancaire";
        $infosPaiement['proprietaireCarte'] = $_POST['proprietaireCarte'];
        $infosPaiement['numCarte'] = $_POST['numCarte'];
        $infosPaiement['dateExpiration'] = $_POST['dateCarte'];
        $infosPaiement['cvv'] = $_POST['cvvCarte'];
        $_SESSION['choixPaiement'] = $infosPaiement;
        header("Location: CommanderConfirmation.php");
        exit();
    } else if (isset($_POST['validerPaypal'])) {
        $infosPaiement['methode'] = "Paypal";
        $infosPaiement['email'] = $_POST['emailPaypal'];
        $infosPaiement['mdp'] = $_POST['mdpPaypal'];
        $_SESSION['choixPaiement'] = $infosPaiement;
        header("Location: CommanderConfirmation.php");
        exit();
    }
}
function afficherFormPaiement($choixPaiement)
{
    echo '<div class="adresse-form">';
    switch ($choixPaiement) {
        case 'carteBancaire':
            echo '<button class="choix-boutton" name="carteBancaire">Carte bancaire</button>';
            echo '<div class="form-group">';
            echo '<label for="proprietaireCarte">Propriétaire :</label>';
            echo '<input type="text" name="proprietaireCarte" class="input-field" pattern="^[\\p{L} \'-]+$" title="Nom du propriétaire invalide." value="' . (isset($_POST['proprietaireCarte']) ? htmlspecialchars($_POST['proprietaireCarte']) : '') . '" required>';
            echo '</div>';
            echo '<div class="form-group">';
            echo '<label for="numCarte">Numéro :</label>';
            echo '<input type="text" name="numCarte" class="input-field" maxlength="16" pattern="\\d{16}" title="Numéro de carte invalide. Exemple : 16 chiffres." value="' . (isset($_POST['numCarte']) ? htmlspecialchars($_POST['numCarte']) : '') . '" required>';
            echo '</div>';
            echo '<div class="form-group">';
            echo '<label for="dateCarte">Date d\'expiration (MM/YYYY) :</label>';
            echo '<input type="text" name="dateCarte" class="input-field" maxlength="7" pattern="(0[1-9]|1[0-2])\/\d{4}" placeholder="MM/YYYY" title="Date d\'expiration invalide. Exemple : MM/YYYY" value="' . (isset($_POST['dateCarte']) ? htmlspecialchars($_POST['dateCarte']) : '') . '" required>';
            echo '</div>';
            echo '<div class="form-group">';
            echo '<label for="cvvCarte">Cvv :</label>';
            echo '<input type="text" name="cvvCarte" class="input-field" maxlength="3" pattern="\\d{3,4}" title="CVV invalide. Exemple : 3 ou 4 chiffres." value="' . (isset($_POST['cvvCarte']) ? htmlspecialchars($_POST['cvvCarte']) : '') . '" required>';
            echo '</div>';
            echo '<button class="choix-boutton" name="validerCarteBancaire">Confirmer</button>';
            break;

        case 'paypal':
            echo '<button class="choix-boutton" name="paypal">Paypal</button>';
            echo '<div class="form-group">';
            echo '<label for="emailPaypal">Adresse e-mail :</label>';
            echo '<input type="email" name="emailPaypal" class="input-field" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}" title="Adresse e-mail invalide." value="' . (isset($_POST['emailPaypal']) ? htmlspecialchars($_POST['emailPaypal']) : '') . '" required>';
            echo '</div>';
            echo '<div class="form-group">';
            echo '<label for="mdpPaypal">Mot de passe :</label>';
            echo '<input type="password" name="mdpPaypal" class="input-field" pattern=".{5,}" title="Le mot de passe ne peut pas être aussi court." required>';
            echo '</div>';
            echo '<button class="choix-boutton" name="validerPaypal">Confirmer</button>';
            break;

        default:
            echo '<button class="choix-boutton" name="carteBancaire">Carte bancaire</button><br><br>
        <button class="choix-boutton" name="paypal">Paypal</button><br><br>
        <button class="choix-boutton" name="magasin">Paiement en magasin</button><br><br>';
            break;
    }
    echo '</div>';
}
?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>Choix de la livraison</title>
    <link rel="stylesheet" href="Css/Commander.css">
</head>

<body>
    <?php
    include 'include/header.php';
    ?>
    <div class="barEtapes">
        <div class="etape" id="etapeAutres">
            <p>Livraison</p>
        </div>
        <div class="etape" id="etapeActuel">
            <p>Paiement</p>
        </div>
        <div class="etape" id="etapeAutres">
            <p>Confirmation</p>
        </div>
    </div>
    <div class="center">
        <p id="titre">Moyen de paiement</h2><br>
            <?php
            // print_r($_SESSION);
            // print_r($_COOKIE);
            ?>
        <form method="POST">
            <?php afficherFormPaiement($choixPaiement); ?>
    </div>
    </form>
    <form method="POST">
        <div class="left">
            <button class="choix-boutton" name="retourPanier">Retourner au panier</button>
            <button class="choix-boutton" name="retour">Retourner à l'étape précédente</button>
            <?php if ($choixPaiement != "") echo '<button class="choix-boutton" id="right" name="autresOptions">Afficher les autres options de paiement</button>'; ?>
        </div><br>
    </form>

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