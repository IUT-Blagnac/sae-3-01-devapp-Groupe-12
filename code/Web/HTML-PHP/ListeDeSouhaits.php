<?php
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
    .wishlist {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 20px;
        padding: 20px;
    }
    .wishlist-produit {
        background-color: #ffffff;
        border: 1px solid #ddd;
        padding: 15px;
        text-align: left;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        position: relative;
    }
    .wishlist-produit img {
        float: left;
        width: 120px;
        height: 120px;
        margin-right: 15px;
        border-radius: 4px;
    }
    .wishlist-produit h3 {
        margin-top: 0;
        color: #333;
        font-size: 18px;
    }
    .wishlist-produit .price {
        font-weight: bold;
        margin-top: 5px;
    }
    .wishlist-produit .description {
        font-size: 14px;
        color: #555;
        margin: 10px 0;
    }
    .wishlist-produit .add-to-cart {
        position: absolute;
        bottom: 15px;
        right: 15px;
    }

    .delete-wishlist-produit {
    border: none;
    background: none;
    cursor: pointer;
    position: absolute;
    top: 10px;
    right: 10px;
}

.delete-wishlist-produit img {
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
<div class="clear-wishlist">
    <button id="clear-wishlist">Vider la Liste de Souhaits</button>
</div>

<div class="wishlist">
    <?php if (!empty($wishlistProducts)): ?>
        <?php foreach ($wishlistProducts as $produit): ?>
            <div class="wishlist-produit">
            <?php
                $nomProduitModifie = modifNomProduit($produit['nomProduit']);
                $imagePath = "img/{$nomProduitModifie}.png"; // Ou .jpg selon le format de vos images
            ?>
            <img src="<?php echo $imagePath; ?>" alt="<?php echo htmlspecialchars($produit['nomProduit']); ?>">
                <div class="product-info">
                    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                    <p class="price"><?= htmlspecialchars($produit['prixVente']) ?> €</p>
                    <p class="description"><?= htmlspecialchars($produit['description']) ?></p>
                </div>
                <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                <button class="delete-wishlist-produit" data-numproduit="<?= $produit['numProduit'] ?>">
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
        setCookie('wishlist', JSON.stringify([]), 30); // on mets un tableau vide dans le cookie
        alert("La liste de souhaits a été vidée.");
        window.location.reload(); // Recharge la page
    });

$(document).ready(function() {
        $('.delete-wishlist-produit').on('click', function() {
            var numProduit = $(this).data('numproduit');
            var wishlist = JSON.parse(getCookie('wishlist') || '[]');
            var index = wishlist.indexOf(numProduit);

            if (index !== -1) {
                wishlist.splice(index, 1); // Supprime l'élément du tableau
                setCookie('wishlist', JSON.stringify(wishlist), 30); // Met à jour le cookie
                $(this).closest('.wishlist-produit').remove(); // Supprime l'élément de l'interface utilisateur
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
