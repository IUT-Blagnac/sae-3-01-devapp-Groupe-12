<?php
// Connexion à la base de données
require_once 'Connect.inc.php';

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Initialisation du panier
$items = [];

// Vérifie si le cookie 'panier' existe
if (isset($_COOKIE['panier'])) {
    // Décode le cookie panier pour obtenir le tableau des produits
    $cart = json_decode($_COOKIE['panier'], true);

    // Boucle sur chaque produit dans le panier
    foreach ($cart as $item) {
        if (isset($item['numProduit'])) {
            // Récupération de l'information produit
            $stmt = $conn->prepare("SELECT numProduit, nomProduit, prixVente, 'img/youtube.png' as image FROM Produit WHERE numProduit = ?");
            $stmt->execute([$item['numProduit']]);
            $product = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($product) {
                $product['quantite'] = $item['quantite']; // Utiliser la quantité du cookie
                $items[] = $product;
            }
        }
    }
}

// Calcule le total du panier
$total = 0;
foreach ($items as $item) {
    $total += $item['prixVente'] * $item['quantite'];
}

?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panier - LaBonneNote</title>

    <style>
        /* Mise en page générale */
        body {
            font-family: 'Arial', sans-serif;
            background: #f3f3f3;
            margin: 0;
            padding: 0;
            line-height: 1.6;
        }

        .container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 20px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
        }

        /* En-tête du panier */
        .cart-header {
            border-bottom: 2px solid #e9e9e9;
            padding-bottom: 10px;
            margin-bottom: 20px;
            background: url('music-note-background.png') no-repeat center center;
            /* Un fond d'éléments de notes de musique */
            background-size: cover;
            color: #333;
        }

        .cart-header h1 {
            text-align: center;
            padding: 20px;
            font-weight: 300;
        }

        /* Liste des articles */
        .cart-items {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .cart-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px dashed #d9d9d9;
        }

        .cart-item:last-child {
            border-bottom: 0;
        }

        .cart-item img {
            width: 80px;
            height: 80px;
            margin-right: 20px;
            border-radius: 50%;
        }

        .item-details {
            flex: 1;
            padding: 10px 0;
        }

        .item-details h4 {
            margin: 0;
            padding: 0;
            color: #333;
        }

        .item-price {
            color: #888;
            font-size: 18px;
        }

        /* Total du panier */
        .cart-total {
            text-align: right;
            font-size: 22px;
            color: #333;
        }

        /* Boutons */
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background: #5cb85c;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }

        .btn:hover {
            background: #4cae4c;
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .cart-item {
                flex-direction: column;
                align-items: center;
            }

            .cart-item img {
                margin-bottom: 10px;
            }

            .item-details {
                text-align: center;
            }
        }

        /* Adaptations spécifiques à la musique */
        .item-details h4:before {
            content: '🎵 ';
            font-size: 24px;
        }

        .quantite-controls {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 10px;
        }

        .quantite-moins,
        .quantite-plus {
            background-color: #e9e9e9;
            border: 1px solid #ccc;
            padding: 5px 10px;
            cursor: pointer;
        }

        .quantite {
            margin: 0 10px;
        }

        .delete-item {
            background: none;
            border: none;
            cursor: pointer;
            padding: 5px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .delete-item img {
            width: 20px;
            height: 20px;
            object-fit: contain;
        }

        .cart-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .commander-bouton {
            padding: 10px 20px;
            text-decoration: none;
            color: black;
            background-color: #a9890a;
            border: none;
            border-radius: 8px;
            font-size: 24px;
            font-weight: bold;
            transition: background-color 0.3s ease, transform 0.2s ease;
            margin: 10px;
            display: inline-block;
        }

        .commander-bouton:hover {
            background-color: #d89f2f;
            transform: scale(1.1);
        }
    </style>
</head>

<body>



    <?php
    $selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
    include 'include/header.php';
    ?>
    <div class="container">
        <h2>Votre panier</h2>
        <?php if (!empty($items)) : ?>
            <?php foreach ($items as $item) : ?>
                <div class="cart-item">
                    <img src="img/youtube.png" alt="<?= htmlspecialchars($item['nomProduit']) ?>">
                    <div class="panier-item-details">
                        <h4><?= htmlspecialchars($item['nomProduit']) ?></h4>
                        <p class="prix-panier" data-price="<?= $item['prixVente'] ?>" data-numproduit="<?= $item['numProduit'] ?>">
                            <?= htmlspecialchars($item['prixVente']) * $item['quantite'] ?> €
                        </p>
                        <div class="quantite-controls">
                            <button class="quantite-moins" data-numproduit="<?= $item['numProduit'] ?>">-</button>
                            <span class="quantite" data-numproduit="<?= $item['numProduit'] ?>"><?= $item['quantite'] ?></span>
                            <button class="quantite-plus" data-numproduit="<?= $item['numProduit'] ?>">+</button>
                        </div>
                        <button class="delete-item" data-numproduit="<?= $item['numProduit'] ?>">
                            <img src="img/delete.png" alt="Supprimer">
                        </button>
                    </div>
                </div>
            <?php endforeach; ?>

            <div class="cart-total">
                Total: <?= number_format($total, 2) ?> €<br>
                <a class="commander-bouton" href="CommanderChoixLivraison.php">Continuer ma commande</a>
            </div>
        <?php else : ?>
            <p>Votre panier est vide.</p>
        <?php endif; ?>
    </div>
    <?php include 'include/footer.php'; ?>

    <script>
        // Fonction pour obtenir la valeur d'un cookie
        function getCookie(name) {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = cookies[i].split('=');
                if (cookie[0].trim() === name) {
                    return cookie[1];
                }
            }
            return "";
        }

        // Fonction pour définir un cookie
        function setCookie(name, value, days) {
            var expires = "";
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = "; expires=" + date.toUTCString();
            }
            document.cookie = name + "=" + (value || "") + expires + "; path=/";
        }

        // Mettre à jour le cookie de panier
        function updateCartCookie(numProduit, newquantite) {
            var cart = JSON.parse(decodeURIComponent(getCookie('panier'))) || [];
            var productIndex = cart.findIndex(item => item.numProduit == numProduit);

            if (newquantite > 0) {
                if (productIndex > -1) {
                    cart[productIndex].quantite = newquantite;
                } else {
                    cart.push({
                        numProduit: numProduit,
                        quantite: newquantite
                    });
                }
            } else {
                cart = cart.filter(item => item.numProduit != numProduit);
            }

            setCookie('panier', JSON.stringify(cart), 7);
            location.reload();
        }

        // Gestion des clics sur les boutons + et -
        document.addEventListener('DOMContentLoaded', function() {
            var minusButtons = document.querySelectorAll('.quantite-moins');
            var plusButtons = document.querySelectorAll('.quantite-plus');

            minusButtons.forEach(button => {
                button.addEventListener('click', function() {
                    var numProduit = this.getAttribute('data-numproduit');
                    var quantiteSpan = document.querySelector('.quantite[data-numproduit="' + numProduit + '"]');
                    var currentquantite = parseInt(quantiteSpan.innerText);

                    if (currentquantite > 1) {
                        updateCartCookie(numProduit, currentquantite - 1);
                    } else {
                        if (confirm('Êtes-vous sûr de vouloir supprimer cet article du panier ?')) {
                            updateCartCookie(numProduit, 0);
                        }
                    }
                });
            });

            plusButtons.forEach(button => {
                button.addEventListener('click', function() {
                    var numProduit = this.getAttribute('data-numproduit');
                    var quantiteSpan = document.querySelector('.quantite[data-numproduit="' + numProduit + '"]');
                    var currentquantite = parseInt(quantiteSpan.innerText);

                    updateCartCookie(numProduit, currentquantite + 1);
                });
            });
        });

        document.addEventListener('DOMContentLoaded', function() {
            // Gestion des clics sur les boutons de suppression
            document.querySelectorAll('.delete-item').forEach(button => {
                button.addEventListener('click', function() {
                    var numProduit = this.getAttribute('data-numproduit');
                    if (confirm('Êtes-vous sûr de vouloir supprimer cet article du panier ?')) {
                        updateCartCookie(numProduit, 0);
                    }
                });
            });

            // Mettre à jour le cookie de panier
            function updateCartCookie(numProduit, newquantite) {
                var cart = JSON.parse(decodeURIComponent(getCookie('panier'))) || [];
                cart = cart.filter(item => item.numProduit != numProduit);

                setCookie('panier', JSON.stringify(cart), 7);
                location.reload();
            }

            // Fonctions pour manipuler les cookies
            function getCookie(name) {
                var cookies = document.cookie.split(';');
                for (var i = 0; i < cookies.length; i++) {
                    var cookie = cookies[i].split('=');
                    if (cookie[0].trim() === name) {
                        return cookie[1];
                    }
                }
                return "";
            }


        });
    </script>


</body>

</html>