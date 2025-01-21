<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Votre Boutique de Musique</title>
    <style>
        .header-container {
            display: flex;
            align-items: center;
            justify-content: space-between;
            background-color: #f8f8f8;
            padding: 10px 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header-slogan {
            flex-grow: 1;
            text-align: left; /* Alignement du texte à gauche */
        }

        .header-links {
            display: flex;
            align-items: center;
            gap: 20px;
            flex-grow: 1;
            justify-content: flex-end; /* Alignement des liens à droite */
        }

        .header-links a,
        .header-links img {
            text-decoration: none;
            color: #333;
            font-weight: bold;
        }

        .logo img,
        .home-link img,
        .cart-link img,
        .user-link img {
            height: 30px;
            cursor: pointer;
        }

        .submenu {
            display: none;
            position: absolute;
            background-color: #f8f8f8;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            padding: 10px;
            list-style-type: none;
            margin: 0;
        }

        .submenu li a {
            text-decoration: none;
            display: block;
            padding: 5px;
            color: #333;
        }

        /* Positionnez correctement votre menu en fonction de votre mise en page */
        .menu-item {
            position: relative;
        }

        @media (max-width: 768px) {
            .header-container {
                flex-direction: column;
                align-items: flex-start;
            }

            .header-slogan,
            .header-links {
                flex-grow: 0;
                justify-content: center;
                width: 100%;
                text-align: center;
            }

            .header-slogan {
                margin-bottom: 10px;
            }
        }
    </style>
</head>
<body>
    <header>
        <div class="header-container">
            <span class="header-slogan">Où la musique trouve sa voix...</span>
            <div class="header-links">
                <a href="index.php" class="home-link">
                    <img src="img/accueil.png" alt="Home">
                </a>
                <?php if (!isset($_SESSION['admin'])): ?>
                    <a href="Panier.php" class="cart-link">
                        <img src="img/panier.png" alt="Panier">
                    </a>
                <?php endif; ?>
                <?php if (isset($_SESSION['user_id']) || isset($_SESSION['admin'])): ?>
                    <div class="menu-item">
                    <a href="javascript:void(0);" id="user-icon">
                        <img src="img/compte.png" alt="Compte" style="height: 50px;">
                    </a>
                    <ul class="submenu" id="user-menu">
                        <li><a href="VotreCompte.php">Votre Compte</a></li>
                        <li><a href="SuiviCommandes.php">Mes Commandes</a></li>
                        <li><a href="ListeDeSouhaits.php">Liste de Souhaits</a></li>
                        <li><a href="Coupons.php">Coupons</a></li>
                        <li><a href="Deconnexion.php">Déconnexion</a></li>
                        <li><a href="ServiceClient.php">Service Client</a></li>
                        
                    </ul>
                </div>
                <?php else: ?>
                    <a href="FormConnexion.php">Se connecter</a>
                <?php endif; ?>
                <a href="index.php" class="logo">
                    <img src="img/logo.jpg" alt="Logo de la boutique">
                </a>
                <?php if (isset($_SESSION['admin'])): ?>
                    <div class="menu-item">
                        <a href="javascript:void(0);" id="admin-icon">
                            Admin
                        </a>
                        <ul class="submenu" id="admin-menu">
                            <li><a href="AjoutProduit.php">Ajout Produit</a></li>
                            <li><a href="SuppressionProduit.php">Suppression Produit</a></li>
                            <li><a href="ModificationProduit.php">Modification Produit</a></li>
                            <li><a href="Promotions.php">Promotions</a></li>
                            <li><a href="Ventes.php">Ventes</a></li>
                        </ul>
                    </div>
                <?php endif; ?>

            </div>
        </div>
    </header>

    <?php if (isset($_SESSION['admin'])): ?>
    <div class="menu-item">
        <a href="javascript:void(0);" id="admin-icon">
            Admin
        </a>
        <ul class="submenu" id="admin-menu">
            <li><a href="AjoutProduit.php">Ajout Produit</a></li>
            <li><a href="SuppressionProduit.php">Suppression Produit</a></li>
            <li><a href="ModificationProduit.php">Modification Produit</a></li>
            <!-- Ajoutez d'autres liens d'administration au besoin -->
        </ul>
    </div>
<?php endif; ?>

<script>
    // Ajoutez ce script juste avant la fermeture de votre balise body
    document.addEventListener('DOMContentLoaded', function() {
        var userIcon = document.getElementById('user-icon');
        var userMenu = document.getElementById('user-menu');
        var adminIcon = document.getElementById('admin-icon'); // Ajout de la variable adminIcon
        var adminMenu = document.getElementById('admin-menu'); // Ajout de la variable adminMenu
        
        var isUserMenuVisible = false; // Variable pour suivre la visibilité du menu utilisateur
        var isAdminMenuVisible = false; // Variable pour suivre la visibilité du menu admin
        
        userIcon.addEventListener('click', function() {
            // Inverser la visibilité du menu utilisateur à chaque clic
            isUserMenuVisible = !isUserMenuVisible;
            userMenu.style.display = isUserMenuVisible ? 'block' : 'none';
            
            // Cacher le menu admin s'il est ouvert
            isAdminMenuVisible = false;
            adminMenu.style.display = 'none';
        });
        
        adminIcon.addEventListener('click', function() {
            // Inverser la visibilité du menu admin à chaque clic
            isAdminMenuVisible = !isAdminMenuVisible;
            adminMenu.style.display = isAdminMenuVisible ? 'block' : 'none';
            
            // Cacher le menu utilisateur s'il est ouvert
            isUserMenuVisible = false;
            userMenu.style.display = 'none';
        });
        
        // Pour fermer le menu si on clique en dehors (pour les deux menus)
        document.addEventListener('click', function(event) {
            var isClickInsideUserMenu = userIcon.contains(event.target) || userMenu.contains(event.target);
            var isClickInsideAdminMenu = adminIcon.contains(event.target) || adminMenu.contains(event.target);
            
            if (!isClickInsideUserMenu && isUserMenuVisible) {
                userMenu.style.display = 'none';
                isUserMenuVisible = false;
            }
            
            if (!isClickInsideAdminMenu && isAdminMenuVisible) {
                adminMenu.style.display = 'none';
                isAdminMenuVisible = false;
            }
        });
        
        // Masquer les menus déroulants si l'utilisateur correspondant n'est pas connecté
        <?php if (!isset($_SESSION['Sgroupe12'])): ?>
        userMenu.style.display = 'none';
        <?php endif; ?>
        
        <?php if (!isset($_SESSION['admin'])): ?>
        adminMenu.style.display = 'none';
        <?php endif; ?>
    });
</script>



</body>
</html>



