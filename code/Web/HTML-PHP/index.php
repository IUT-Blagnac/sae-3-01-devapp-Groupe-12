<?php


// Connexion à la base de données
require_once 'Connect.inc.php';

// Fonction pour récupérer les produits
function getProducts($conn, $section) {
    $stmt = $conn->prepare("SELECT * FROM Produit WHERE section = ? LIMIT 10");
    $stmt->execute([$section]);
    return $stmt->fetchAll(PDO::FETCH_ASSOC) ?: []; // Return an empty array if no products are found
}

$bestSellers = getProducts($conn, 'best-sellers');
$promotions = getProducts($conn, 'promotions');
$newArrivals = getProducts($conn, 'new');
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

    </style>
</head>
<body>

<?php include 'include/header.php'; ?>

<section class="product-section">
    <h2>Les plus vendus</h2>
    <div class="product-carousel">
        <?php foreach ($bestSellers as $produit): ?>
            <div class="product">
                <img src="img/produits/<?= $produit['image'] ?>" alt="<?= $produit['nomProduit'] ?>">
                <h3><?= $produit['nom'] ?></h3>
                <p><?= $produit['prix'] ?> €</p>
                <button type="button" class="add-to-cart">Ajouter au panier</button>
            </div>
        <?php endforeach; ?>
    </div>
</section>

<section class="product-section">
    <h2>Promotions</h2>
    <div class="product-carousel">
        <?php foreach ($promotions as $produit): ?>
            <img src="img/produits/<?= $produit['image'] ?>" alt="<?= $produit['nomProduit'] ?>">
                <h3><?= $produit['nom'] ?></h3>
                <p><?= $produit['prix'] ?> €</p>
                <button type="button" class="add-to-cart">Ajouter au panier</button>
        <?php endforeach; ?>
    </div>
</section>

<section class="product-section">
    <h2>Nouveautés</h2>
    <div class="product-carousel">
        <?php foreach ($newArrivals as $produit): ?>
            <div class="product">
                <img src="img/produits/<?= $produit['image'] ?>" alt="<?= $produit['nomProduit'] ?>">
                <h3><?= $produit['nom'] ?></h3>
                <p><?= $produit['prix'] ?> €</p>
                <button type="button" class="add-to-cart">Ajouter au panier</button>
            </div>
        <?php endforeach; ?>
    </div>
</section>


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

</body>
</html>


