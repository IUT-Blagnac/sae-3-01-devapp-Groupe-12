<?php
// Connexion à la base de données
require_once 'Connect.inc.php';

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    header("Location: FormConnexion.php");
    exit;
}

if (isset($_SESSION['admin']) && $_SESSION['admin'] == "oui") {
    header("Location: FormConnexion.php");
    exit;
}

function modifNomProduit($name)
{
    $name = str_replace(' ', '_', $name); // Remplace les espaces par des underscores
    $name = strtolower($name); // Convertit en minuscules

    // Tableau de correspondance pour la suppression des accents
    $accents = array(
        'À' => 'A', 'Á' => 'A', 'Â' => 'A', 'Ã' => 'A', 'Ä' => 'A', 'Å' => 'A', 'à' => 'a', 'á' => 'a', 'â' => 'a', 'ã' => 'a', 'ä' => 'a', 'å' => 'a',
        'Ò' => 'O', 'Ó' => 'O', 'Ô' => 'O', 'Õ' => 'O', 'Ö' => 'O', 'Ø' => 'O', 'ò' => 'o', 'ó' => 'o', 'ô' => 'o', 'õ' => 'o', 'ö' => 'o', 'ø' => 'o',
        'È' => 'E', 'É' => 'E', 'Ê' => 'E', 'Ë' => 'E', 'è' => 'e', 'é' => 'e', 'ê' => 'e', 'ë' => 'e',
        'Ç' => 'C', 'ç' => 'c',
        'Ì' => 'I', 'Í' => 'I', 'Î' => 'I', 'Ï' => 'I', 'ì' => 'i', 'í' => 'i', 'î' => 'i', 'ï' => 'i',
        'Ù' => 'U', 'Ú' => 'U', 'Û' => 'U', 'Ü' => 'U', 'ù' => 'u', 'ú' => 'u', 'û' => 'u', 'ü' => 'u',
        'ÿ' => 'y',
        'Ñ' => 'N', 'ñ' => 'n',
        'Ÿ' => 'Y',
        'Æ' => 'AE', 'æ' => 'ae',
        'Œ' => 'OE', 'œ' => 'oe',
        'ß' => 'ss'
    );

    // Remplacement des caractères accentués
    foreach ($accents as $accent => $replacement) {
        $name = str_replace($accent, $replacement, $name);
    }

    return $name;
}

// Initialisation du panier
$items = [];

// Vérifiez si le panier existe dans la session pour l'utilisateur actuel
if (isset($_SESSION['panier'][$_SESSION['user_id']])) {
    $cart = $_SESSION['panier'][$_SESSION['user_id']];

    foreach ($cart as $item) {
        if (isset($item['numProduit'])) {
            // Récupération de l'information produit
            $stmt = $conn->prepare("SELECT numProduit, nomProduit, prixVente, 'img/youtube.png' as image FROM Produit WHERE numProduit = ?");
            $stmt->execute([$item['numProduit']]);
            $product = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($product) {
                $product['quantite'] = $item['quantite']; // Utiliser la quantité de la session
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
                <?php
                $nomProduitModifie = modifNomProduit($item['nomProduit']);
                $imagePath = "img/{$nomProduitModifie}.png";
                ?>
                <div class="cart-item">
                    <img src="<?php echo $imagePath; ?>" alt="<?php echo htmlspecialchars($item['nomProduit']); ?>">
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
                        <form method="POST" action="Panier.php">
                            <input type="hidden" name="deleteProduct" value="<?= $item['numProduit'] ?>">
                            <button class="delete-item" type="submit">
                                <img src="img/delete.png" alt="Supprimer">
                            </button>
                        </form>
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
                        updateCartSession(numProduit, currentquantite - 1);
                    } else {
                        if (confirm('Êtes-vous sûr de vouloir supprimer cet article du panier ?')) {
                            updateCartSession(numProduit, 0);
                        }
                    }
                });
            });

            plusButtons.forEach(button => {
                button.addEventListener('click', function() {
                    var numProduit = this.getAttribute('data-numproduit');
                    var quantiteSpan = document.querySelector('.quantite[data-numproduit="' + numProduit + '"]');
                    var currentquantite = parseInt(quantiteSpan.innerText);

                    updateCartSession(numProduit, currentquantite + 1);
                });
            });

            // Fonction pour mettre à jour le panier en session
            function updateCartSession(numProduit, newquantite) {
                // Utilisez une requête AJAX ou la méthode fetch pour mettre à jour le panier en session côté serveur
                // Exemple d'utilisation de fetch :
                fetch('update_cart.php', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            numProduit: numProduit,
                            quantite: newquantite,
                        }),
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            // Mise à jour réussie, rafraîchissez la page ou mettez à jour l'affichage du panier
                            location.reload();
                        } else {
                            alert('Une erreur s\'est produite lors de la mise à jour du panier.');
                        }
                    })
                    .catch(error => {
                        console.error('Erreur de mise à jour du panier :', error);
                    });
            }

            // Fonction pour supprimer un article du panier
        });

        document.addEventListener('DOMContentLoaded', function() {
    // Gestion des clics sur les boutons de suppression
    document.querySelectorAll('.delete-item').forEach(button => {
        button.addEventListener('click', function() {
            var numProduit = this.getAttribute('data-numproduit');
            if (confirm('Êtes-vous sûr de vouloir supprimer cet article du panier ?')) {
                // Envoyer une requête AJAX pour retirer le produit de la session
                fetch('delete_cart_item.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        numProduit: numProduit,
                    }),
                })
                .then(response => response.json())
                .then(data => {
                    console.log(data); // Affichez la réponse pour le débogage

                    if (data.success) {
                        // Suppression réussie, rafraîchissez la page ou mettez à jour l'affichage du panier
                        location.reload();
                    } else {
                        alert('Une erreur s\'est produite lors de la suppression de l\'article du panier.');
                    }
                })
                .catch(error => {
                    console.error('Erreur de suppression de l\'article du panier :', error);
                });
            }
        });
    });
});
    </script>
    <?php print_r($_SESSION); ?>
</body>

</html>