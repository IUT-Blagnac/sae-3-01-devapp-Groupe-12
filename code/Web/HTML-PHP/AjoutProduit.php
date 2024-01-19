<?php
session_start();
if (!isset($_SESSION['admin']) || $_SESSION['admin'] != "oui") {
    header("Location: index.php");
    exit();
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

$msg = "";

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

    if (!empty($_FILES['ficImg']) and $_FILES['ficImg']['error'] == 0) {
        $infosfichier = pathinfo($_FILES['ficImg']['name']);
        $extension_upload = $infosfichier['extension'];
        $extensions_autorisees = ['png'];
        $tailleMax = 100 * 1024;
        if ($_FILES['ficImg']['size'] <= $tailleMax) {
            if (in_array($extension_upload, $extensions_autorisees)) {
                $nomImage = modifNomProduit($nomProduit);

                move_uploaded_file($_FILES['ficImg']['tmp_name'], 'img/' . $nomImage . ".png");

                $stmt = $conn->prepare("INSERT INTO Produit (coutAchat, prixVente, codeBarre, referenceInterne, nomProduit, seuilReapprovisionnement, stockMax, fraisSupplementaires, numCategorie, codeType, numRegroupement, numProduitCompose, section, numSousCategorie, numCategorieInferieur, stock, description, numProduit) 
                    VALUES (:coutAchat, :prixVente, :codeBarre, :referenceInterne, :nomProduit, :seuilReapprovisionnement, :stockMax, :fraisSupplementaires, :numCategorie, :codeType, :numRegroupement, :numProduitCompose, :section, :numSousCategorie, :numCategorieInferieur, :stock, :description, :numProduit)");

                // Récupérer le prochain numéro de produit
                $stmt_max = $conn->prepare("SELECT MAX(numProduit) AS maxNumProduit FROM Produit");
                $stmt_max->execute();
                if ($row = $stmt_max->fetch(PDO::FETCH_ASSOC)) {
                    $maxNumProduit = $row['maxNumProduit'];
                    $numProduit = $maxNumProduit + 1;
                } else {
                    $numProduit = 1; // Si la table est vide
                }

                $stmt->execute([
                    ':coutAchat' => $coutAchat,
                    ':prixVente' => $prixVente,
                    ':codeBarre' => $codeBarre,
                    ':referenceInterne' => $referenceInterne,
                    ':nomProduit' => $nomProduit,
                    ':seuilReapprovisionnement' => $seuilReapprovisionnement,
                    ':stockMax' => $stockMax,
                    ':fraisSupplementaires' => $fraisSupplementaires,
                    ':numCategorie' => $numCategorie,
                    ':codeType' => $codeType,
                    ':numRegroupement' => $numRegroupement,
                    ':numProduitCompose' => $numProduitCompose,
                    ':section' => $section,
                    ':numSousCategorie' => $numSousCategorie,
                    ':numCategorieInferieur' => $numCategorieInferieur,
                    ':stock' => $stock,
                    ':description' => $description,
                    ':numProduit' => $numProduit
                ]);

                // Rediriger vers une page de confirmation ou de gestion des produits
                header("Location: AjoutProduit.php");
                exit();
            } else {
                $msg = "Extension non valide, merci de sélectionner un fichier PNG.\n";
            }
        } else {
            $msg = "Fichier image trop gros et/ou non png... Recommencez\n";
        }
    } else {
        $msg = "Erreur : aucune image n'a été sélectionnée.\n";;
    }
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
            grid-template-columns: 1fr;
            /* Une seule colonne pour tout */
            gap: 10px;
            /* Espace entre les éléments du formulaire */
        }

        label {
            font-weight: bold;
            display: block;
            /* S'assure que le label prend toute la largeur */
            margin-bottom: 5px;
            /* Espace en dessous de chaque label */
        }

        input[type="text"],
        select,
        textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            margin-bottom: 20px;
            /* Espace après chaque champ de formulaire */
        }

        /* Style pour le bouton Ajouter le Produit */
        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            padding: 10px 20px;
            margin-top: 10px;
            /* Espace avant le bouton */
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>

<body>
    <div class="container">
        <?php echo $msg; ?>
        <h2>Ajouter un Produit</h2>
        <form action="AjoutProduit.php" method="post" enctype="multipart/form-data">
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
            <select id="codeType" name="codeType">
                <option value="ELE">ELE</option>
                <option value="KEY">KEY</option>
                <option value="PRC">PRC</option>
                <option value="STR">STR</option>
                <option value="WND">WND</option>
            </select><br>

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

            <label for="Image">Image :</label>
            <td><input size="35" type="file" name="ficImg" required></td>
            <input type="submit" value="Ajouter le Produit">
        </form>
    </div>
    <?php
    include 'include/footer.php';
    ?>
</body>

</html>
