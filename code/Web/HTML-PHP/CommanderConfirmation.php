<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
require_once 'Connect.inc.php';
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
} else {
    foreach ($cart as $item) {
        if (isset($item['numProduit'])) {
            // Récupération de l'information produit
            $stmt = $conn->prepare("SELECT numProduit, nomProduit, prixVente, fraisSupplementaires, 'img/youtube.png' as image FROM Produit WHERE numProduit = ?");
            $stmt->execute([$item['numProduit']]);
            $product = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($product) {
                $product['quantite'] = $item['quantite'];
                $items[] = $product;
            }
        }
    }
    $total = 0;
    foreach ($items as $item) {
        $total += $item['prixVente'] * $item['quantite'];
    }
}
$choixAdresse = "";
$totalLivraison = 0;
if (!isset($_SESSION['choixLivraison'])) {
    echo "<script>
    alert(\"Merci de passer par le choix de la livraison et le paiement avant de passer à la confirmation.\");
    window.location.href = 'CommanderChoixLivraison.php'</script>";
} else {
    if ($_SESSION['choixLivraison'] == "retraitMagasin") {
        $choixAdresse = "Retrait en magasin";
        $totalLivraison = 0;
    } else {
        $req = $conn->prepare("SELECT * FROM AdresseLivraison WHERE numClient = :numClient");
        $req->bindParam(':numClient', $_SESSION['numClient']);
        $req->execute();
        $resAdresse = $req->fetchAll();
        $choixAdresse = "(" . $resAdresse[0]['LibelleLivraison'] . ")<br><br>";
        $choixAdresse .= $resAdresse[0]['adrLivraison'] . "<br>";
        $choixAdresse .= $resAdresse[0]['adrPostaleLivraison'] . " ";
        $choixAdresse .= $resAdresse[0]['adrVilleLivraison'] . "<br>";
        $choixAdresse .= $resAdresse[0]['adrPaysLivraison'];
        $totalLivraison = 8;
    }
}
$choixPaiement = "";
if (!isset($_SESSION['choixPaiement'])) {
    echo "<script>
    alert(\"Merci de choisir le moyen de paiement avant de passer à la confirmation de la commande.\");
    window.location.href = 'CommanderChoixPaiement.php'</script>";
} else {
    switch ($_SESSION['choixPaiement']['methode']) {
        case 'Carte bancaire':
            $choixPaiement = "Carte bancaire";
            break;
        case 'Paypal':
            $choixPaiement = "Paypal";
            break;
        case 'Paiement en magasin':
            $choixPaiement = "Paiement en magasin";
            break;
        default:
            echo "<script>
        alert(\"Merci de passer par le choix de la livraison et le paiement avant de passer à la confirmation.\");
        window.location.href = 'CommanderChoixLivraison.php'</script>";
            break;
    }
}

$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';

$total = 0;
$totalFrais = 0;
foreach ($items as $item) {
    $totalFrais += $item['fraisSupplementaires'] * $item['quantite'];
    $total += $item['prixVente'] * $item['quantite'];
}
$total += $totalFrais + $totalLivraison;

if (isset($_POST['commander'])) {
    try {
        $stmt = $conn->prepare("INSERT INTO Commande (dateCommande, montantFrais, montant, numClient, idAdresseLivraison, typeLivraison, statut) VALUES ('2023-12-31', 5, 50, :numClient, 5, 'Retrait magasin', 'En cours')");
        $stmt->execute(['numClient' => $_SESSION['numClient']]);
        $numCommandeInserted = $conn->lastInsertId();
        // $panier = $_SESSION['panier'];

        foreach ($items as $item) {
            $numProduit = $item['numProduit'];
            $quantiteCommandee = $item['quantite'];
            
            $stmt = $conn->prepare("INSERT INTO LigneCde (numProduit, numCommande, quantiteCommandee) VALUES (?, ?, ?)");
            $stmt->execute([$numProduit, $numCommandeInserted, $quantiteCommandee]);
        }

        
        $stmt = $conn->prepare("INSERT INTO Paiement (numCommande, montantTotal, statut, typePaiement) VALUES (?, ?, ?, ?)");
        $stmt->execute([$numCommandeInserted, $total, 'accepté', $choixPaiement]);

        // sendMail();
        setcookie('panier', '', time() - 3600, '/');
        echo "<script>
        alert(\"Merci pour votre commande !\");
        window.location.href = 'index.php'</script>";
    } catch (PDOException $e) {
        if ($e->getCode() === '45000') {
            echo "<script>
            alert(\"Commande impossible, stock insuffisant !\");
            window.location.href = 'Panier.php'</script>";
        } else {
            echo $e;
            echo "<script>
            alert(\"Une erreur s'est produite lors de la commande, veuillez réessayer.\nCode d'erreur : " . $e->getCode() . ");
            window.location.href = 'Panier.php'</script>";
        }
    }
} else {
    if (isset($_POST['retourPanier'])) {
        unset($_SESSION['choixLivraison']);
        unset($_SESSION['choixPaiement']);
        header("Location: Panier.php");
        exit();
    } else if (isset($_POST['retour'])) {
        unset($_SESSION['choixPaiement']);
        header("Location: CommanderChoixPaiement.php");
        exit();
    }
}

?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>Confirmation de la commande</title>
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
        <div class="etape" id="etapeAutres">
            <p>Paiement</p>
        </div>
        <div class="etape" id="etapeActuel">
            <p>Confirmation</p>
        </div>
    </div>
    <div class="center">
        <?php
        // print_r($_SESSION);
        // print_r($_COOKIE);
        ?>
        <p id="titre">Récapitulatif</p><br>
        <div class="recap">
            <div class="recapCommande">
                <p id="recapTitre">Votre choix de livraison :</p>
                <?php
                echo '<p id="recapChoix">' . $choixAdresse . '</p>';
                ?>
            </div>
            <div class="recapCommande">
                <p id="recapTitre">Votre choix de paiement :</p>
                <?php
                echo '<p id="recapChoix">' . $choixPaiement . '</p>';
                ?>
            </div>
            <div class="recapCommande" id="recapProduits">
                <p id="recapTitre">Vos produits :</p>
                <table class="tableau-produits">
                    <thead>
                        <tr>
                            <th>Désignation : </th>
                            <th>Prix unitaire :</th>
                            <th>Frais supplémentaires :</th>
                            <th>Quantité :</th>
                        </tr><br>
                    </thead>
                    <tbody>
                        <?php foreach ($items as $item) : ?>
                            <tr>
                                <td>
                                    <img src="img/youtube.png" alt="<?= htmlspecialchars($item['nomProduit']) ?>">
                                    <p id="recapChoix"><?= htmlspecialchars($item['nomProduit']) ?></p>
                                </td>
                                <td>
                                    <p id="recapChoix"><?php echo $item['prixVente'] . ' €'; ?></p>
                                </td>
                                <td>
                                    <p id="recapChoix"><?php echo $item['fraisSupplementaires'] . ' €'; ?></p>
                                </td>
                                <td>
                                    <p id="recapChoix"><?php echo $item['quantite']; ?></p>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
                <?php
                echo '<p id="total1">Frais des produits : ' . $totalFrais . ' € </p>';
                echo '<p id="total1">Livraison : ' . $totalLivraison . ' € </p>';
                echo '<p id="total">Total : ' . $total . ' € </p>';
                ?>
            </div>
        </div><br><br>
    </div>
    <form method="POST">
        <div class="left">
            <button class="choix-boutton" name="retourPanier">Retourner au panier</button>
            <button class="choix-boutton" name="retour">Retourner à l'étape précédente</button>
            <button class="choix-boutton" id="commanderButt" name="commander">Commander</button>
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

<?php

// Méthode utilisée à la base pour envoyer le mail de confirmation de la commande, mais le serveur de l'iut bloquant la communication avec les serveurs gmails, impossible d'envoyer le mail.

// Utiliser les classes nécessaires de PHPMailer
// use PHPMailer\PHPMailer\PHPMailer;
// use PHPMailer\PHPMailer\Exception;
// use PHPMailer\PHPMailer\SMTP;

// require 'PHPMailer/src/Exception.php';
// require 'PHPMailer/src/PHPMailer.php';
// require 'PHPMailer/src/SMTP.php';


// function sendMail()
// {

//     try {
//         $mail = new PHPMailer(true);

//         $mail->isSMTP();
//         $mail->SMTPDebug = 2;
//         $mail->isSMTP();
//         $mail->Host = 'smtp.gmail.com';
//         $mail->SMTPAuth = true;
//         $mail->Username = 'labonnenote.service@gmail.com';
//         $mail->Password = 'efkv liiw gxwd dmvv';
//         $mail->SMTPSecure = 'tls';
//         $mail->Port = 587;

//         // Paramètres de l'email
//         $mail->setFrom('labonnenote.service@gmail.com', 'azdadaz'); // Votre adresse Gmail et nom
//         $mail->addAddress('ahmad31100@live.fr', 'Nom Destinataire'); // Adresse du destinataire
//         $mail->isHTML(true);
//         $mail->Subject = 'Confirmation de commande';

//         // Corps de l'email avec le récapitulatif de la commande
//         $message = '
//         <div class="center">
//         </div>';

//         $mail->Body = $message;

//         // Envoi de l'email
//         $mail->send();
//         echo 'Email envoyé avec succès!';
//     } catch (Exception $e) {
//         echo "Erreur lors de l'envoi de l'email: {$mail->ErrorInfo}";
//     }
// }
?>