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
function modifNomProduit($name) {
    $name = str_replace(' ', '_', $name); // Remplace les espaces par des underscores
    $name = strtolower($name); // Convertit en minuscules

    // Tableau de correspondance pour la suppression des accents
    $accents = array(
        'À'=>'A', 'Á'=>'A', 'Â'=>'A', 'Ã'=>'A', 'Ä'=>'A', 'Å'=>'A', 'à'=>'a', 'á'=>'a', 'â'=>'a', 'ã'=>'a', 'ä'=>'a', 'å'=>'a',
        'Ò'=>'O', 'Ó'=>'O', 'Ô'=>'O', 'Õ'=>'O', 'Ö'=>'O', 'Ø'=>'O', 'ò'=>'o', 'ó'=>'o', 'ô'=>'o', 'õ'=>'o', 'ö'=>'o', 'ø'=>'o',
        'È'=>'E', 'É'=>'E', 'Ê'=>'E', 'Ë'=>'E', 'è'=>'e', 'é'=>'e', 'ê'=>'e', 'ë'=>'e',
        'Ç'=>'C', 'ç'=>'c',
        'Ì'=>'I', 'Í'=>'I', 'Î'=>'I', 'Ï'=>'I', 'ì'=>'i', 'í'=>'i', 'î'=>'i', 'ï'=>'i',
        'Ù'=>'U', 'Ú'=>'U', 'Û'=>'U', 'Ü'=>'U', 'ù'=>'u', 'ú'=>'u', 'û'=>'u', 'ü'=>'u',
        'ÿ'=>'y',
        'Ñ'=>'N', 'ñ'=>'n',
        'Ÿ'=>'Y',
        'Æ'=>'AE', 'æ'=>'ae',
        'Œ'=>'OE', 'œ'=>'oe',
        'ß'=>'ss'
    );

    // Remplacement des caractères accentués
    foreach ($accents as $accent => $replacement) {
        $name = str_replace($accent, $replacement, $name);
    }

    return $name;
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
$nomProduitModifie = modifNomProduit($produit['nomProduit']);
$imagePath = "img/{$nomProduitModifie}.png"; // Ou .jpg selon le format de vos images
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

.leave-review {
    margin-top: 20px;
    padding: 15px;
    background-color: #f7f7f7;
    border-radius: 5px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.leave-review h3 {
    margin-bottom: 10px;
    color: #333;
}

.leave-review form {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.leave-review label {
    margin-bottom: 5px;
    font-weight: bold;
}

.leave-review input[type="text"],
.leave-review textarea,
.leave-review select {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.leave-review textarea {
    resize: vertical;
    min-height: 100px;
}

.leave-review button {
    background-color: #4CAF50;
    color: white;
    padding: 10px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
}

.leave-review button:hover {
    background-color: #45a049;
}


    </style>
</head>
<body>
<?php include 'include/header.php'; ?>

<div class="product-details">
<img src="<?php echo $imagePath; ?>" alt="<?php echo htmlspecialchars($produit['nomProduit']); ?>">
    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
    <p><?= htmlspecialchars($produit['prixVente']) ?> €</p>
    <p><?= htmlspecialchars($produit['description']) ?></p>
    <?php
        // Vérifier si l'utilisateur connecté a déjà laissé un avis
$stmt = $conn->prepare("SELECT COUNT(*) FROM Avis WHERE numProduit = ? AND numClient = ?");
$stmt->execute([$numProduit, $_SESSION['numClient']]);
$canReview = $stmt->fetchColumn() == 0;

// Afficher un message d'erreur si l'utilisateur a déjà laissé un avis
if (isset($_GET['error']) && $_GET['error'] == 'alreadyReviewed') {
    echo "<p>Vous avez déjà laissé un avis pour ce produit.</p>";
}

if ($canReview) {
    // Afficher le formulaire d'avis
        echo '<div class="leave-review">';
        echo '<h3>Laissez un avis</h3>';
        echo '<form action="laisserAvis.php" method="post">';
        echo '<input type="hidden" name="numProduit" value="' . $numProduit . '">';
        echo '<label for="note">Note :</label>';
        echo '<select name="note" id="note">';
        for ($i = 1; $i <= 5; $i++) {
            echo "<option value=\"$i\">$i</option>";
        }
        echo '</select>';
        echo '<label for="commentaire">Commentaire :</label>';
        echo '<textarea name="commentaire" id="commentaire" rows="4"></textarea>';
        echo '<button type="submit">Soumettre lavis</button>';
        echo '</form>';
        echo '</div>';
}
    ?>

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
