<?php
// Connexion à la base de données
require_once 'Connect.inc.php';

// Fonction pour récupérer les produits en fonction d'une section
function getProducts($conn, $section) {
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE section = ? LIMIT 10");
    $stmt->execute([$section]);
    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Fonction pour récupérer tous les produits selon les critères demandées
function getProduitFiltre($conn, $recherche = null, $categorie = null, $trie = null) {
    $query = "SELECT Produit.* FROM Produit 
              LEFT JOIN SousCategorie ON Produit.numSousCategorie = SousCategorie.numSousCategorie";

    $params = [];
    $conditions = [];

    if (!empty($recherche)) {
        $conditions[] = "(Produit.nomProduit LIKE ? OR SousCategorie.libelleSousCategorie LIKE ?)";
        $params[] = '%' . $recherche . '%';
        $params[] = '%' . $recherche . '%';
    }

    if (!empty($categorie)) {
        $conditions[] = "SousCategorie.libelleSousCategorie = ?";
        $params[] = $categorie;
    }

    if ($conditions) {
        $query .= " WHERE " . implode(' AND ', $conditions);
    }

    if ($trie) {
        switch ($trie) {
            case 'price-asc':
                $query .= " ORDER BY prixVente ASC";
                break;
            case 'price-desc':
                $query .= " ORDER BY prixVente DESC";
                break;
            case 'popularity-asc':
                $query .= " ORDER BY notation ASC";
                break;
            case 'popularity-desc':
                $query .= " ORDER BY notation DESC";
                break;
        }
    }
    
    $stmt = $conn->prepare($query);
    $stmt->execute($params);
    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Récupération des produits par sections
$bestSellers = getProducts($conn, 'bestSellers');
$promotions = getProducts($conn, 'promotions');
$newArrivals = getProducts($conn, 'new');

// Recherche de produits si le terme de recherche est passé
$recherche = isset($_GET['search']) ? $_GET['search'] : null;
//Recherche de produits si catégorie sélectionné
$categorie = isset($_GET['category']) ? $_GET['category'] : null;
//Trie des produits rechercher
$trie = isset($_GET['sort-by']) ? $_GET['sort-by'] : null;
//Récupère la catégorie sélectionné pour la sauvegarder afin de la garder après un refresh de la page par exemple
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
//Affiche les carrousels si on ne recherche rien et que la catégorie actuelle est "Tout nos produits"
$showCarousels = empty($recherche) && (empty($categorie) || $categorie == 'Tous nos produits');
if ($categorie == 'Tous nos produits' || empty($categorie)) {
    // Récupérez tous les produits
    $products = getProduitFiltre($conn,$recherche,$trie);
} else {
    // Récupérez les produits filtrés
    $products = getProduitFiltre($conn, $recherche, $categorie,$trie);
}
// Récupérer la liste de souhaits du cookie
$wishlist = json_decode($_COOKIE['wishlist'] ?? '[]', true);

?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - LaBonneNote</title>
    <link rel="stylesheet" type="text/css" href="//cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>
    <link rel="stylesheet" href="styles.css">
    <!-- Ajoutez votre propre CSS ici pour styliser la page et les produits -->
    <style>
                body {
            font-family: Arial, sans-serif;
        }

        .main-content {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-around;
            padding: 20px;
        }
        .product-category {
            margin: 10px;
            width: calc(33% - 20px);
        }
        .product-category h2 {
            background-color: #f7f7f7;
            padding: 10px;
            border-radius: 5px;
            text-align: center;
        }
        .product-item {
            background-color: #ffffff;
            border: 1px solid #ddd;
            padding: 10px;
            margin-bottom: 15px;
            text-align: center;
        }
        .product-item img {
            max-width: 100%;
            height: auto;
        }
        .product-item p {
            color: #555;
            margin: 5px 0;
        }
        .product-item .price {
            color: #B12704;
        }
        .product-item .old-price {
            text-decoration: line-through;
        }


        #help-circle {
    position: fixed;
    bottom: 50px;
    right: 50px;
    border-radius: 50%;
    width: 60px; /* Taille du bouton '?' */
    height: 60px; /* Taille du bouton '?' */
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
    z-index: 100;
    background-color: white; /* ou autre couleur de fond souhaitée */
}

#help-circle span {
    font-size: 24px;
    color: black;
}

.circle-content {
    display: none;
    position: absolute;
    bottom: 100%;
    right: 50%;
    transform: translate(50%, 50%);
    width: 0;
    height: 0;
    z-index: 99;
}

.circle-link {
    position: absolute;
    width: 40px; /* Taille des éléments circulaires */
    height: 40px; /* Taille des éléments circulaires */
    border-radius: 50%;
    background-color: #f0f0f0;
    /* Centrez le contenu du lien */
    display: flex;
    align-items: center;
    justify-content: center;
    /* Supprimez les marges ou le padding si présent */
    margin: 0;
    padding: 0;
    /* Réinitialisez d'autres styles qui pourraient affecter la position */
    top: 0;
    left: 0;
    /* Assurez-vous que transform-origin est centré pour une rotation correcte */
    transform-origin: center center;
}



.circle-link img {
    width: 100%;
    height: auto;
    border-radius: 50%; /* Les images sont également circulaires */
}


.products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    padding: 20px;
}

.product-item {
    background-color: #ffffff;
    border: 1px solid #ddd;
    padding: 10px;
    text-align: center;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.search-input {
    min-width: 500px; /* Ou toute autre largeur appropriée */
}

.wishlist-button {
        border: none;
        background: none;
        cursor: pointer;
        padding: 5px; /* Ajustez au besoin */
        display: inline-block; /* Assurez-vous que le bouton ne prend pas toute la largeur */
    }

    .wishlist-button img {
        width: 24px; /* Ajustez la taille de l'icône ici */
        height: auto; /* Conserve les proportions de l'image */
    }

    .product-link {
    color: inherit; /* Utilise la couleur de texte actuelle */
    text-decoration: none; /* Supprime le soulignement */
}

.product-link:hover {
    text-decoration: none; /* Supprime le soulignement au survol */
}


    </style>
</head>
<body>

<?php include 'include/header.php'; ?>
<?php if ($showCarousels): ?>
<section class="product-section">
    <h2>Les plus vendus</h2>
    <div class="product-carousel">
        <?php foreach ($bestSellers as $produit): ?>

                <div class="product">
                    <a href="detailsProduit.php?numProduit=<?= $produit['numProduit'] ?>" class="product-link">
                        <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
                        <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                    </a>
                    <p><?= $produit['prixVente'] ?> €</p>
                    <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                    <button class="wishlist-button" data-numproduit="<?= $produit['numProduit'] ?>">
                        <?php if (in_array($produit['numProduit'], $wishlist)): ?>
                            <img src="img/coeur-rempli.png" alt="Dans la liste de souhaits">
                        <?php else: ?>
                            <img src="img/coeur.png" alt="Ajouter à la liste de souhaits">
                        <?php endif; ?>
                    </button>

                </div>

        <?php endforeach; ?>
    </div>
</section>

<section class="product-section">
    <h2>Promotions</h2>
   <div class="product-carousel">
        <?php foreach ($promotions as $produit): ?>

            <div class="product">
                <a href="detailsProduit.php?numProduit=<?= $produit['numProduit'] ?>" class="product-link">
                    <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
                    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                </a>
                <p><?= $produit['prixVente'] ?> €</p>
                <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                <button class="wishlist-button" data-numproduit="<?= $produit['numProduit'] ?>">
                    <?php if (in_array($produit['numProduit'], $wishlist)): ?>
                        <img src="img/coeur-rempli.png" alt="Dans la liste de souhaits">
                    <?php else: ?>
                        <img src="img/coeur.png" alt="Ajouter à la liste de souhaits">
                    <?php endif; ?>
                </button>

            </div>

        <?php endforeach; ?>
    </div>
</section>

<section class="product-section">
    <h2>Nouveautés</h2>
    <div class="product-carousel">
        <?php foreach ($newArrivals as $produit): ?>

            <div class="product">
                <a href="detailsProduit.php?numProduit=<?= $produit['numProduit'] ?>" class="product-link">
                    <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
                    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                </a>
                <p><?= $produit['prixVente'] ?> €</p>
                <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                <button class="wishlist-button" data-numproduit="<?= $produit['numProduit'] ?>">
                    <?php if (in_array($produit['numProduit'], $wishlist)): ?>
                        <img src="img/coeur-rempli.png" alt="Dans la liste de souhaits">
                    <?php else: ?>
                        <img src="img/coeur.png" alt="Ajouter à la liste de souhaits">
                    <?php endif; ?>
                </button>

            </div>

        <?php endforeach; ?>
    </div>
</section>
<?php endif; ?>

<div class="sorting-container">
    <label for="sort-by">Trier par:</label>
    <select id="sort-by" name="sort-by">
        <option value="price-asc" <?= $trie == 'price-asc' ? 'selected' : '' ?>>Prix Croissant</option>
        <option value="price-desc" <?= $trie == 'price-desc' ? 'selected' : '' ?>>Prix Décroissant</option>
        <option value="popularity-asc" <?= $trie == 'popularity-asc' ? 'selected' : '' ?>>Popularité Croissante</option>
        <option value="popularity-desc" <?= $trie == 'popularity-desc' ? 'selected' : '' ?>>Popularité Décroissante</option>
    </select>
</div>

<section class="all-produits">
    <h2>Tous nos produits</h2>
    <div class="products-grid">
        <?php foreach ($products as $produit): ?>

            <div class="product-item">
                <a href="detailsProduit.php?numProduit=<?= $produit['numProduit'] ?>" class="product-link">
                    <img src="img/youtube.png" alt="<?= htmlspecialchars($produit['nomProduit']) ?>">
                    <h3><?= htmlspecialchars($produit['nomProduit']) ?></h3>
                </a>
                <p><?= htmlspecialchars($produit['prixVente']) ?> €</p>
                <button type="button" class="add-to-cart" data-numproduit="<?= $produit['numProduit'] ?>">Ajouter au panier</button>
                <button class="wishlist-button" data-numproduit="<?= $produit['numProduit'] ?>">
                    <?php if (in_array($produit['numProduit'], $wishlist)): ?>
                        <img src="img/coeur-rempli.png" alt="Dans la liste de souhaits">
                    <?php else: ?>
                        <img src="img/coeur.png" alt="Ajouter à la liste de souhaits">
                    <?php endif; ?>
                </button>

            </div>

        <?php endforeach; ?>
    </div>
</section>

<div id="help-circle">
    <span>?</span>
    <div class="circle-content">
        <a href="aPropos.php" class="circle-link" style="--angle: 0;"><img src="img/apropos.png" alt="À propos"></a>
        <a href="ServiceClient.php" class="circle-link" style="--angle: 90;"><img src="img/service.png" alt="Service"></a>
    </div>
</div>





<?php include 'include/footer.php'; ?>

<!-- Inclure jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Inclure Slick Carousel -->
<script type="text/javascript" src="//cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script>
$(document).ready(function(){
    $('.product-carousel').slick({
        infinite: true,
        slidesToShow: 3,
        slidesToScroll: 1,
        nextArrow: '<button type="button" class="slick-next">Suivant</button>',
        prevArrow: '<button type="button" class="slick-prev">Précédent</button>',
        responsive: [
            {
                breakpoint: 768,
                settings: {
                    slidesToShow: 1
                }
            }
        ]
    });
});
</script>
<script>
// Ajouter un écouteur d'événement sur tous les boutons "Ajouter au panier"
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


$(document).ready(function() {
    // Toggle the display of the circular menu on click
    $('#help-circle').on('click', function() {
        $('.circle-content').toggle();
        if ($('.circle-content').is(':visible')) {
            positionCircleLinks();
        }
    });
});

function positionCircleLinks() {
    var radius = 80; // Réduisez ou augmentez pour ajuster la distance du centre
    var startAngle = 160; // Angle de départ pour la position des éléments
    var increment = 45; // Angle d'incrément entre les images

    $('.circle-link').each(function(i) {
        var angle = startAngle + (i * increment);
        var radians = angle * Math.PI / 180;
        var x = radius * Math.cos(radians); // Coordonnée X
        var y = radius * Math.sin(radians); // Coordonnée Y

        // Appliquez les coordonnées en tenant compte de la taille des éléments
        $(this).css({
            'transform': 'translate(' + x + 'px, ' + y + 'px)'
        });
    });
}

//Traitement de la recherche 
$(document).ready(function() {
    // Soumission du formulaire lorsque la catégorie change
    $('.category-select').on('change', function() {
        var selectedCategory = $(this).val();
        var selectedCategoryText = $(this).find('option:selected').text();

        // Met à jour le champ de recherche avec le nom de la sous-catégorie
        $('#search-form').submit();
    });

    // Gestion de la soumission du formulaire de recherche
    $('#search-form').on('submit', function(e) {
        e.preventDefault(); // Empêche le rechargement de la page

        // Redirige vers index.php avec les paramètres de recherche et de catégorie
        var recherche = $('input[name="search"]').val().trim();
        var selectedCategory = $('select[name="category"]').val();
        var queryParams = $.param({ search: recherche, category: selectedCategory });
        window.location.href = 'index.php?' + queryParams;
    });
});



$('.wishlist-button').on('click', function() {
    var numProduit = $(this).data('numproduit');
    var wishlist = JSON.parse(getCookie('wishlist') || '[]');
    var index = wishlist.indexOf(numProduit);

    if (index !== -1) {
        // Si le produit est déjà dans la wishlist, on le retire
        wishlist.splice(index, 1);
        $(this).find('img').attr('src', 'img/coeur.png'); // Changez l'icône en cœur vide
    } else {
        // Sinon, on l'ajoute à la wishlist
        wishlist.push(numProduit);
        $(this).find('img').attr('src', 'img/coeur-rempli.png'); // Changez l'icône en cœur rempli
    }

    setCookie('wishlist', JSON.stringify(wishlist), 30); // Met à jour le cookie
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

<script>
//Gestion du tri
document.addEventListener('DOMContentLoaded', function() {
    // Écouteur d'événement pour la sélection du critère de tri
    document.getElementById('sort-by').addEventListener('change', function() {
        var sortBy = this.value;
        var queryParams = new URLSearchParams(window.location.search);
        queryParams.set('sort-by', sortBy);
        window.location.search = queryParams.toString();
    });
});
</script>


</body>
</html>


