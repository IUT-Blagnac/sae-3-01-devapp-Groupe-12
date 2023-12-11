<?php
// Démarrage ou récupération de la session
session_start();
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Votre Boutique de Musique</title>
    <style>
        /* Styles principaux */
        .header-container {
            display: flex;
            align-items: center;
            justify-content: center; 
            background-color: #131921;
            color: white;
            margin: 0 auto; 
            padding: 0 20px; 
            height: 70px; 
        }

        .logo, .cart {
            position: absolute;
        }

        .logo {
            left: 20px; 
        }

        .cart {
            right: 20px; 
        }

        .logo img, .cart img {
            max-height: 50px; 
        }

        .search-bar {
            display: flex;
            align-items: center;
            justify-content: flex-end; 
            background-color: #fff;
            border-radius: 4px;
            margin: 0 auto; 
            padding: 0; 
            width: 100%; 
            max-width: 700px;
        }

        .category-select, .search-input, .search-button {
            height: 40px; 
        }

        /* Styles pour le select */
        .category-select {
            background-color: #f3f3f3;
            color: #555;
            border: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            appearance: none;
            border-radius: 4px 0 0 4px; 
        }

        /* Ajout d'une flèche pour le select */
        .category-select::after {
            content: '▼';
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
            color: #555;
        }

        .search-input {
            flex-grow: 1;
            border: none;
            padding-left: 10px; 
        }

        .search-button {
            background-color: #febd69;
            border: none;
            border-radius: 0 4px 4px 0; 
            cursor: pointer;
            width: 40px; 
        }

        .search-button img {
            width: 100%; 
            height: auto; 
        }

        /* Styles pour la navigation principale */
        .main-nav {
            display: flex;
            justify-content: flex-end;
            position: absolute;
            right: 90px; 
            height: 70px; 
        }

        .main-nav ul {
            list-style-type: none;
            display: flex;
            margin: 0;
            padding: 0;
            align-items: center; 
        }

        .main-nav ul li {
            padding: 0 10px;
        }

        .main-nav ul li a {
            text-decoration: none;
            color: white;
            font-weight: bold;
        }


/* Styles pour le menu déroulant */
.menu-item {
    position: relative; /* Position relative pour le menu déroulant */
}

.submenu {
    display: none;
    position: absolute;
    background-color: #131921;
    box-shadow: 0 8px 16px rgba(0,0,0,0.2);
    border-radius: 0px 0px 4px 4px;
    padding: 0;
    z-index: 100;
    right: 0; /* Ajouté pour aligner le menu déroulant avec l'élément parent */
}

.submenu li {
    display: block;
    text-align: left; /* Alignement du texte à gauche */
}

.submenu li a {
    color: white;
    padding: 10px;
    text-decoration: none;
    display: block;
}

.menu-item:hover .submenu {
    display: block; /* Affichage du sous-menu au survol */
}

        /* Styles responsives */
        @media (max-width: 768px) {
            .header-container {
                flex-direction: column;
                text-align: center;
            }

            .logo, .cart {
                position: relative;
                padding: 10px 0;
            }

            .search-bar {
                order: -1; 
                width: 100%; 
                max-width: none; 
                margin-top: 10px; 
            }

            .main-nav {
                position: relative;
                width: 100%;
                justify-content: center;
                margin-top: 10px;
            }

            .logo img, .cart img {
                max-height: 40px;
            }
        }
    </style>
</head>
<body>
<header>
    <div class="header-container">
        <a href="/" class="logo">
            <img src="img/logo.jpg" alt="Logo de la boutique">
        </a>
        <div class="search-bar">
            <select name="categories" class="category-select">
                <option value="all">Toutes nos catégories  ▼</option>
                <option value="Cordes">Cordes</option>
                <option value="Percussions">Percussions</option>
                <option value="Clavier">Clavier</option>
                <option value="Vent">Vent</option>
                <option value="Livres">Livres</option>
                <option value="Matériel Studio">Matériel Studio</option>
            </select>
            <input type="text" placeholder="Rechercher LaBonneNote.fr" class="search-input">
            <button type="submit" class="search-button">
                <img src="img/loupe.png" alt="Rechercher">
            </button>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="/ServiceClient.php">Service Client</a></li>
                <li><a href="/APropos.php">A Propos</a></li>
                <?php
                if (isset($_SESSION['Sgroupe12'])) {
                    echo '<li class="menu-item">
                              <a href="#">Compte</a>
                              <ul class="submenu">
                              <li><a href="VotreCompte.php">Votre Compte</a></li>
                              <li><a href="ListeDeSouhait.php">Liste de Souhait</a></li>
                              <li><a href="Coupons.php">Coupons</a></li>
                              <li><a href="Deconnexion.php">Déconnexion</a></li>
                              </ul>
                          </li>';
                } else {
                    echo '<li><a href="FormConnexion.php">Connexion</a></li>';
                }
                ?>
            </ul>
        </nav>
        <div class="cart">
            <a href="/panier">
                <img src="img/panier.jpg" alt="Panier d'achat">
            </a>
        </div>
    </div>
</header>
</body>
</html>
