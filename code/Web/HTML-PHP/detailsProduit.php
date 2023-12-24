<?php
// Connexion à la base de données
require_once 'Connect.inc.php';

// Récupérer l'identifiant du produit depuis l'URL
$numProduit = $_GET['numProduit'] ?? null;

if (!$numProduit) {
    echo "Produit non spécifié.";
    exit; // Ou rediriger vers une autre page.
}

// Récupération des détails du produit
$stmt = $conn->prepare("SELECT * FROM Produit WHERE numProduit = ?");
$stmt->execute([$numProduit]);
$produit = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$produit) {
    echo "Produit non trouvé.";
    exit; // Ou afficher un message approprié.
}

// Ici on stock l'url du produit actuel qui va servir pour le partage des produits
$urlProduit = "http://193.54.227.208/~saephp12/detailsProduit.php?numProduit=" . $numProduit;

// Récupération des avis pour ce produit
$stmt = $conn->prepare("SELECT Avis.*, Client.pseudoClient FROM Avis JOIN Client ON Avis.numClient = Client.numClient WHERE numProduit = ?");
$stmt->execute([$numProduit]);
$avis = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Récupération des réponses aux avis avec les pseudos des clients
$reponses = [];
foreach ($avis as $unAvis) {
    $stmt = $conn->prepare("SELECT ReponseAvis.*, Client.pseudoClient FROM ReponseAvis JOIN Client ON ReponseAvis.numClient = Client.numClient WHERE numAvis = ?");
    $stmt->execute([$unAvis['numAvis']]);
    $reponses[$unAvis['numAvis']] = $stmt->fetchAll(PDO::FETCH_ASSOC);
}

?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= htmlspecialchars($produit['nomProduit']) ?></title>
    <link rel="stylesheet" href="styles.css">
    <style>
        /* Styles pour la page de détails du produit */
.product-details {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
    border: 1px solid #ddd;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.product-details img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 0 auto 20px;
}

.product-details h3 {
    color: #333;
    margin-bottom: 10px;
}

.product-details p {
    color: #555;
    line-height: 1.6;
}

.add-to-cart {
    background-color: #ff9900;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    display: block;
    width: 100%;
    max-width: 200px;
    margin: 20px auto 0;
}

 /* Styles pour le bouton partager */
.share-icon {
    width: 24px; 
    height: 24px; 
    margin: 0px !important;
}

.bouton-share{
    height: 40px;
    width: 40px;
    display: flex;
    align-items: center;
    justify-content: space-around;
}

.shareOption {
        display: inline-block; 
        margin-right: 10px; 
}

.shareOption img {
        width: 20px; 
        height: 20px; 
        margin-right: 5px; 
        vertical-align: left; 
    }

/* Styles pour les avis */
.product-reviews {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
    border-top: 1px solid #ddd;
}

.review {
    padding: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 10px;
}

.review h4 {
    margin: 0;
    color: #007600;
}

.response {
    padding-left: 20px;
    border-left: 3px solid #f0f0f0;
    margin-top: 5px;
}

.toggle-responses {
    margin-top: 10px;
    background-color: #dddddd;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
}


    </style>
</head>
<body>
<?php include 'include/header.php'; ?>

<div class="product-details">
    <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
    <p><?= htmlspecialchars($produit['prixVente']) ?> €</p>
    <p><?= htmlspecialchars($produit['description']) ?></p>

    <!-- Boutons de partage sur les réseaux sociaux -->
    <div class="product-share">
    <button id="shareButton" class="bouton-share">
        <img class="share-icon" src="img/img_share.png" alt="Partager ce produit">
    </button>
        <div id="shareOptions" style="display: none;">
            <a href="#" class="shareOption" data-network="facebook"><img src="img/facebook.png" alt="Facebook"></a>
            <a href="#" class="shareOption" data-network="twitter"><img src="img/twitter.png" alt="Twitter"></a>
            <a href="#" class="shareOption" data-network="instagram"><img src="img/instagram.png" alt="Instagram"></a>
        </div>
    </div>

    <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
</div>

<div class="product-reviews">
    <?php foreach ($avis as $unAvis): ?>
        <div class="review">
            <h4><?= htmlspecialchars($unAvis['pseudoClient']) ?></h4>
            <p>Note: <?= htmlspecialchars($unAvis['note']) ?>/5</p>
            <p><?= htmlspecialchars($unAvis['commentaire']) ?></p>
            <?php if (isset($reponses[$unAvis['numAvis']]) && count($reponses[$unAvis['numAvis']]) > 0): ?>
                <button class="toggle-responses" data-avis="<?= $unAvis['numAvis'] ?>">Afficher les réponses (<?= count($reponses[$unAvis['numAvis']]) ?>)</button>
                <div class="responses" id="responses-<?= $unAvis['numAvis'] ?>" style="display: none;">
                    <?php foreach ($reponses[$unAvis['numAvis']] as $reponse): ?>
                        <div class="response">
                            <p><?= htmlspecialchars($reponse['pseudoClient']) ?>: <?= htmlspecialchars($reponse['contenuReponse']) ?></p>
                        </div>
                    <?php endforeach; ?>
                </div>
            <?php endif; ?>
        </div>
    <?php endforeach; ?>
</div>


<?php include 'include/footer.php'; ?>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    
   $('.add-to-cart').on('click', function() {
    var numProduit = $(this).data('numproduit');

    $.ajax({
        url: 'addToCart.php',
        type: 'POST',
        data: { numProduit: numProduit },
        success: function(response) {
            alert(response);
            
        },
        error: function() {
            alert("Erreur lors de l'ajout au panier.");
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.toggle-responses').forEach(function(button) {
        button.addEventListener('click', function() {
            var avisId = this.getAttribute('data-avis');
            var responsesDiv = document.getElementById('responses-' + avisId);
            responsesDiv.style.display = responsesDiv.style.display === 'none' ? 'block' : 'none';

            
        });
    });
    var shareButton = document.getElementById('shareButton');
    var shareOptions = document.getElementById('shareOptions');

    // On affiche les options de partage lorsque le bouton est cliqué
    shareButton.addEventListener('click', function () {
        shareOptions.style.display = (shareOptions.style.display === 'none') ? 'block' : 'none';
    });

    // On gère le clic sur une option de partage
    document.querySelectorAll('.shareOption').forEach(function (option) {
        option.addEventListener('click', function (event) {
            event.preventDefault();

            var network = this.getAttribute('data-network');
            var shareUrl;

            // On génère l'URL de partage spécifique au réseau social
            switch (network) {
                case 'facebook':
                    shareUrl = 'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent('<?= $urlProduit ?>');
                    break;
                case 'twitter':
                    shareUrl = 'https://twitter.com/intent/tweet?url=' + encodeURIComponent('<?= $urlProduit ?>');
                    break;
                case 'instagram':
                    // Instagram ne prend pas en charge le partage direct depuis un site web
                    alert('Veuillez partager manuellement sur Instagram.');
                    return;
            }

            // On redirige vers l'URL de partage spécifique au réseau social
            window.open(shareUrl, '_blank');
        });
    });
});
</script>
</body>
</html>
