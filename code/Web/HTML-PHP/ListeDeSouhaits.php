<?php
// Inclusion de la connexion à la base de données
require_once 'Connect.inc.php';

// Fonction pour récupérer les produits de la liste de souhaits
function getWishlistProducts($conn, $wishlist) {
    if (count($wishlist) === 0) {
        // Retourner un tableau vide si la liste de souhaits est vide
        return [];
    }

    $placeholders = implode(',', array_fill(0, count($wishlist), '?'));
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE numProduit IN ($placeholders)");
    $stmt->execute($wishlist);
    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}


// Récupérer la liste de souhaits du cookie
$wishlist = json_decode($_COOKIE['wishlist'] ?? '[]');
$wishlistProducts = getWishlistProducts($conn, $wishlist);
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste de Souhaits</title>
    <link rel="stylesheet" href="styles.css">
    <style>
    .wishlist-container {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 20px;
        padding: 20px;
    }
    .wishlist-item {
        background-color: #ffffff;
        border: 1px solid #ddd;
        padding: 15px;
        text-align: left;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        position: relative;
    }
    .wishlist-item img {
        float: left;
        width: 120px;
        height: 120px;
        margin-right: 15px;
        border-radius: 4px;
    }
    .wishlist-item h3 {
        margin-top: 0;
        color: #333;
        font-size: 18px;
    }
    .wishlist-item .price {
        font-weight: bold;
        margin-top: 5px;
    }
    .wishlist-item .description {
        font-size: 14px;
        color: #555;
        margin: 10px 0;
    }
    .wishlist-item .add-to-cart {
        position: absolute;
        bottom: 15px;
        right: 15px;
    }

    .delete-wishlist-item {
    border: none;
    background: none;
    cursor: pointer;
    position: absolute;
    top: 10px;
    right: 10px;
}

.delete-wishlist-item img {
    width: 20px; /* ou la taille souhaitée */
    height: auto;
}

#clear-wishlist {
    padding: 10px 15px;
    background-color: #f44336; /* Couleur rouge pour le bouton de suppression */
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 20px;
    display: block; /* Pour centrer le bouton */
    margin-left: auto;
    margin-right: auto;
}


</style>

</head>
<body>
<?php include 'include/header.php'; ?>
<div class="clear-wishlist-container">
    <button id="clear-wishlist">Vider la Liste de Souhaits</button>
</div>

<div class="wishlist-container">
    <?php if (!empty($wishlistProducts)): ?>
        <?php foreach ($wishlistProducts as $produit): ?>
            <div class="wishlist-item">
                <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
                <div class="product-info">
                    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                    <p class="price"><?= htmlspecialchars($produit['prixVente']) ?> €</p>
                    <p class="description">Brève description du produit...</p>
                    <!-- Vous pouvez ajouter plus de détails ici -->
                </div>
                <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                <button class="delete-wishlist-item" data-numproduit="<?= $produit['numProduit'] ?>">
                    <img src="img/delete.png" alt="Supprimer">
                </button>
            </div>
        <?php endforeach; ?>
    <?php else: ?>
        <p>Votre liste de souhaits est vide.</p>
    <?php endif; ?>
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

document.getElementById('clear-wishlist').addEventListener('click', function() {
        setCookie('wishlist', JSON.stringify([]), 30); // Réinitialiser le cookie avec un tableau vide
        alert("La liste de souhaits a été vidée.");
        window.location.reload(); // Recharger la page pour refléter les changements
    });

$(document).ready(function() {
        $('.delete-wishlist-item').on('click', function() {
            var numProduit = $(this).data('numproduit');
            var wishlist = JSON.parse(getCookie('wishlist') || '[]');
            var index = wishlist.indexOf(numProduit);

            if (index !== -1) {
                wishlist.splice(index, 1); // Supprime l'élément du tableau
                setCookie('wishlist', JSON.stringify(wishlist), 30); // Met à jour le cookie
                $(this).closest('.wishlist-item').remove(); // Supprime l'élément de l'interface utilisateur
            }
        });
    });

    function setCookie(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "")  + expires + "; path=/";
    }

    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }
</script>

</body>
</html>
