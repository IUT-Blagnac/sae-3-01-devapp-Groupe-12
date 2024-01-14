<?php
session_start();
if (!isset($_SESSION['admin'])) {
    header("Location: index.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    require_once 'Connect.inc.php';

    // Récupération des données du formulaire
    $coutAchat = $_POST["coutAchat"];
    $prixVente = $_POST["prixVente"];
    $codeBarre = $_POST["codeBarre"];
    $referenceInterne = $_POST["referenceInterne"];
    $nomProduit = $_POST["nomProduit"];
    $seuilReapprovisionnement = $_POST["seuilReapprovisionnement"];
    $stockMax = $_POST["stockMax"];
    $fraisSupplementaires = $_POST["fraisSupplementaires"];
    $numCategorie = $_POST["numCategorie"];
    $codeType = $_POST["codeType"];
    $numRegroupement = $_POST["numRegroupement"];
    $numProduitCompose = $_POST["numProduitCompose"];
    $section = $_POST["section"];
    $numSousCategorie = $_POST["numSousCategorie"];
    $numCategorieInferieur = $_POST["numCategorieInferieur"];
    $stock = $_POST["stock"];
    $description = $_POST["description"];

    // Insérer le produit dans la base de données
    $stmt = $conn->prepare("INSERT INTO Produit (coutAchat, prixVente, codeBarre, referenceInterne, nomProduit, seuilReapprovisionnement, stockMax, fraisSupplementaires, numCategorie, codeType, numRegroupement, numProduitCompose, section, numSousCategorie, numCategorieInferieur, stock, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->execute([$coutAchat, $prixVente, $codeBarre, $referenceInterne, $nomProduit, $seuilReapprovisionnement, $stockMax, $fraisSupplementaires, $numCategorie, $codeType, $numRegroupement, $numProduitCompose, $section, $numSousCategorie, $numCategorieInferieur, $stock, $description]);

    // Rediriger vers une page de confirmation ou de gestion des produits
    header("Location: AjoutProduit.php");
    exit();
}
?>
<?php
include 'include/header.php'; 
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajouter un Produit</title>
    <style> 
/* Réinitialisation des styles par défaut pour tous les éléments */
/* Réinitialisation des styles par défaut pour tous les éléments */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* Styles pour le formulaire */
.container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

form {
    display: grid;
    grid-template-columns: 1fr; /* Une seule colonne pour tout */
    gap: 10px; /* Espace entre les éléments du formulaire */
}

label {
    font-weight: bold;
    display: block; /* S'assure que le label prend toute la largeur */
    margin-bottom: 5px; /* Espace en dessous de chaque label */
}

input[type="text"],
select,
textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    margin-bottom: 20px; /* Espace après chaque champ de formulaire */
}

/* Style pour le bouton Ajouter le Produit */
input[type="submit"] {
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    padding: 10px 20px;
    margin-top: 10px; /* Espace avant le bouton */
}

input[type="submit"]:hover {
    background-color: #45a049;
}





    </style>
</head>
<body>
    <div class="container">
        <h2>Ajouter un Produit</h2>
        <form action="insert_product.php" method="post">
            <label for="coutAchat">Coût d'achat :</label>
            <input type="text" id="coutAchat" name="coutAchat" required><br>

            <label for="prixVente">Prix de vente :</label>
            <input type="text" id="prixVente" name="prixVente" required><br>

            <label for="codeBarre">Code Barre :</label>
            <input type="text" id="codeBarre" name="codeBarre" required><br>

            <label for="referenceInterne">Référence Interne :</label>
            <input type="text" id="referenceInterne" name="referenceInterne" required><br>

            <label for="nomProduit">Nom du Produit :</label>
            <input type="text" id="nomProduit" name="nomProduit" required><br>

            <label for="seuilReapprovisionnement">Seuil de Reapprovisionnement :</label>
            <input type="text" id="seuilReapprovisionnement" name="seuilReapprovisionnement" required><br>

            <label for="stockMax">Stock Max :</label>
            <input type="text" id="stockMax" name="stockMax" required><br>

            <label for="fraisSupplementaires">Frais Supplémentaires :</label>
            <input type="text" id="fraisSupplementaires" name="fraisSupplementaires" required><br>

            <label for="numCategorie">Numéro de Catégorie :</label>
            <select id="numCategorie" name="numCategorie">
                <option value="1">Catégorie 1</option>
                <option value="2">Catégorie 2</option>
                <option value="3">Catégorie 3</option>
            </select><br>

            <label for="codeType">Code Type :</label>
            <input type="text" id="codeType" name="codeType" required><br>

            <label for="numRegroupement">Numéro de Regroupement :</label>
            <input type="text" id="numRegroupement" name="numRegroupement" required><br>

            <label for="numProduitCompose">Numéro du Produit Composé :</label>
            <input type="text" id="numProduitCompose" name="numProduitCompose" required><br>

            <label for="section">Section :</label>
            <select id="section" name="section">
                <option value="Promotions">Promotions</option>
                <option value="New">New</option>
                <option value="Best-Sellers">Best-Sellers</option>
            </select><br>

            <label for="numSousCategorie">Numéro de Sous-Catégorie :</label>
            <select id="numSousCategorie" name="numSousCategorie">
                <option value="1">Sous-Catégorie 1</option>
                <option value="2">Sous-Catégorie 2</option>
                <option value="3">Sous-Catégorie 3</option>
                <option value="4">Sous-Catégorie 4</option>
                <option value="5">Sous-Catégorie 5</option>
                <option value="6">Sous-Catégorie 6</option>
                <option value="7">Sous-Catégorie 7</option>
            </select><br>

            <label for="numCategorieInferieur">Numéro de Catégorie Inférieur :</label>
            <select id="numCategorieInferieur" name="numCategorieInferieur">
                <option value="1">Catégorie Inférieur 1</option>
                <option value="2">Catégorie Inférieur 2</option>
                <option value="3">Catégorie Inférieur 3</option>
            </select><br>

            <label for="stock">Stock :</label>
            <input type="text" id="stock" name="stock" required><br>

            <label for="description">Description :</label>
            <textarea id="description" name="description" rows="4" cols="50" required></textarea><br>

            <input type="submit" value="Ajouter le Produit">
        </form>
    </div>
    <?php
include 'include/footer.php'; 
?>
</body>
</html>
