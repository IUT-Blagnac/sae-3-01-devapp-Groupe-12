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
require_once('Connect.inc.php');
if (empty($cart)) {
    echo "<script>
    alert(\"Votre panier ne doit pas être vide pour passer une commande. Vous allez être redirigé vers la page d'accueil.\");
    window.location.href = 'index.php'</script>";
    exit();
} else {
    $message = "";
    foreach ($cart as $item) {
        if (isset($item['numProduit'])) {
            $stmt = $conn->prepare("SELECT numProduit, nomProduit, prixVente, stock FROM Produit WHERE numProduit = ?");
            $stmt->execute([$item['numProduit']]);
            $product = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($product) {
                $product['quantite'] = $item['quantite'];

                $quantiteRestante = $product['stock'];

                if ($product['quantite'] > $product['stock']) {
                    $message .= "Le stock ne suffit pas pour le produit {$product['nomProduit']}. Quantité disponible : {$quantiteRestante}. Quantité sélectionnée : {$product['quantite']}\n";
                }

                $product['quantiteRestante'] = $quantiteRestante;
                $items[] = $product;
            }
        }
    }
    $message = str_replace(array("\n", "\r", "\r\n"), "\\n", $message);
    if (!empty($message)) {
        echo "<script>
    alert('" . $message . "');
    window.location.href = 'Panier.php';
    </script>";
        exit();
    }
}


$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';

$afficherAdresses = false;

if (isset($_POST['retourPanier'])) {
    unset($_SESSION['choixLivraison']);
    header("Location: Panier.php");
    exit();
} else if (isset($_POST['ajouterAdresse'])) {
    unset($_SESSION['choixLivraison']);
    header("Location: AjouterAdresseLivraison.php");
    exit();
} else if (isset($_POST['retraitMagasin'])) {
    $_SESSION['choixLivraison'] = "retraitMagasin";
    header("Location: CommanderChoixPaiement.php");
    exit();
} else if (isset($_POST['choixDomicile'])) {
    $afficherAdresses = true;
} else {
    $afficherAdresses = false;
    unset($_SESSION['choixLivraison']);
}

if (isset($_POST['choixAdresse'])) {
    if (isset($_POST['listeAdresses'])) {
        if ($_POST['listeAdresses'] != "Aucune adresse") {
            $_SESSION['choixLivraison'] = $_POST['listeAdresses'];
            header("Location: CommanderChoixPaiement.php");
            exit();
        } else {
            echo "<script>
            alert(\"Merci de sélectionnesr une adresse de livraison pour continuer la commande.\");</script>";
        }
    }
}

function getClientAdresses()
{
    require('Connect.inc.php');
    $idClient = $_SESSION['numClient'];
    try {
        $req = $conn->prepare("SELECT idAdresseLivraison, LibelleLivraison FROM AdresseLivraison WHERE numClient = ?");
        $req->execute([$idClient]);
        return $req->fetchAll(PDO::FETCH_ASSOC);
    } catch (\PDOException $th) {
        echo $th;
        return null;
    }
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
        <div class="etape" id="etapeActuel">
            <p>Livraison</p>
        </div>
        <div class="etape" id="etapeAutres">
            <p>Paiement</p>
        </div>
        <div class="etape" id="etapeAutres">
            <p>Confirmation</p>
        </div>
    </div>
    <div class="center">
        <p id="titre">Choix de la livraison</p><br>
        <?php
        // print_r($_SESSION);
        // print_r($_COOKIE);
        ?>
        <form method="POST">
            <button class="choix-boutton" name="retraitMagasin">Retrait magasin (Gratuit)</button><br><br>
            <button class="choix-boutton" name="choixDomicile">Livraison à domicile (8€)</button><br><br>
            <div class="adresse-livraison" <?php if (!$afficherAdresses) {
                                                echo ' style="display: none;"';
                                            } ?>>
                <p id="titre">Adresse de livraison</p>
                <div class="align-mid">
                    <h3>Mes adresses :</h3>
                    <select class="select" name="listeAdresses">
                        <?php
                        $AdressesClient = getClientAdresses();
                        if (count($AdressesClient) > 0) {
                            foreach ($AdressesClient as $key) {
                                echo '<option value="' . htmlspecialchars($key['idAdresseLivraison']) . '">' . htmlspecialchars($key['LibelleLivraison']) . '</option>';
                            }
                        } else {
                            echo '<option value="Aucune adresse">Aucune adresse</option>';
                        }
                        ?>
                    </select>
                    <button class="choix-boutton" name="ajouterAdresse">Ajouter une adresse</button>
                </div>
                <button class="choix-boutton" name="choixAdresse">Choisir cette adresse</button>
            </div>
    </div>
    <div class="left">
        <button class="choix-boutton" name="retourPanier">Retourner au panier</button>
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

<!DOCTYPE html>
<html lang="fr">