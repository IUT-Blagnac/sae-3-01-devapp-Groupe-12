<?php
$recherche = isset($_GET['search']) ? $_GET['search'] : null;
$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>À Propos de Nous</title>
    <link rel="stylesheet" href="Css/CSS_aPropos.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poppins|Georgia&display=swap">  
</head>
<body>
<?php include 'include/header.php'; ?>
    <div class="grande-box">
    <h1>Histoire de l'entreprise</h1>

    <p>Bienvenue chez laBonneNote !</p>

    <p>L'histoire de "La Bonne Note" commence en 2005 lorsque quatre amis passionnés de musique,  Alexandre,  Andriy, 
        Lilian et  Louis, décident de créer leur propre entreprise spécialisée
        dans la vente d'instruments de musique. Ils ont tous une solide expérience
        dans le domaine, que ce soit en jouant d'un instrument, en travaillant dans
        des magasins de musique ou en réparant des instruments.
        En 2007, "La Bonne Note" ouvre son premier magasin physique à
        Toulouse. Le magasin propose une large gamme d'instruments de musique,
        des guitares aux pianos, et devient rapidement un lieu de prédilection pour les
        musiciens de la région.
        De 2012 jusqu'en 2020, "La Bonne Note" connaît une croissance
        continue. Le magasin de Toulouse prospère et attire une clientèle fidèle grâce
        à son expertise, sa variété d'instruments et son service exceptionnel.
        L'entreprise atteint un chiffre d'affaires record pendant cette période,
        consolidant ainsi sa position dans l'industrie de la musique.
        En 2023, "La Bonne Note" envisage de créer un site d'e-commerce
        pour étendre sa présence en ligne. Ce site permettra aux clients de
        commander des instruments de musique en ligne, ainsi que des livres
        d'apprentissage pour divers instruments. Cette initiative s'inscrit dans la vision
        de "La Bonne Note" d'inspirer la passion pour la musique et de servir sa
        clientèle de manière encore plus complète.
    </p>

    <h2>Notre Mission</h2>

    <div><p><h3>Les Missions de La Bonne Note : Inspirez votre Passion Musicale</h3></br>

Bienvenue chez La Bonne Note, votre complice musical depuis 2005 ! Notre mission est de cultiver et de partager la passion de la musique en proposant une expérience unique à chaque mélomane,</br> amateur ou professionnel.</br> 

</div>
<container class="harmonie">
    <div>
        <h3>Élargir l'Harmonie Musicale</br></h3>
        </br>
Au cœur de notre mission, nous aspirons à élargir l'horizon musical de chacun. Que vous soyez un virtuose accompli ou un novice curieux, La Bonne Note s'engage à vous accompagner dans votre parcours musical. Notre boutique physique à Toulouse est le point de départ, et notre site d'e-commerce en cours de création sera la porte d'entrée vers une diversité d'instruments et de ressources éducatives, accessibles à tous, partout en France.</br>
    </div>
    <div class="harmonieimg">
        <img src="img/harmonie_img.png" alt="harmonie">
    </div>
</container>

<container class="creativite">
    <div class="creativiteimg">
        <img src="img/creativite_img.png" alt="creativite">
    </div>
    <div>
        <h3>Inspirer la Créativité</br></h3>
    
Nous croyons que la musique est une forme d'expression universelle. Notre mission est d'inspirer la créativité musicale en mettant à votre disposition une gamme complète d'instruments de qualité et de livres d'apprentissage soigneusement sélectionnés. La Bonne Note veut être le catalyseur de vos inspirations musicales, qu'elles soient naissantes ou bien établies.</br>
    </div>
</container>

<container class="accessibilite">
    <div>
        <h3>Favoriser l'Accessibilité</br></h3>
    
La musique transcende les frontières, et chez La Bonne Note, nous souhaitons que notre passion soit accessible à tous, peu importe où vous vous trouvez. Notre futur site d'e-commerce a pour mission de rendre nos produits et conseils disponibles à l'échelle nationale. Plus qu'une boutique en ligne, il sera une plateforme facilitant votre exploration musicale et simplifiant l'acquisition de vos instruments préférés.</br>
    </div>
    <div class="accessibiliteimg">
        <img src="img/accessibilite_img.png" alt="accessibilite">
    </div>
</container>

<container class="durable">
    <div class="durableimg">
        <img src="img/durable_img.png" alt="durable">
    </div>
    <div>
        <h3>Établir des Connexions Durables</br></h3>
    
Au-delà des transactions, notre mission consiste à tisser des liens durables avec notre clientèle. La Bonne Note se veut être bien plus qu'un fournisseur d'instruments ; nous sommes une communauté passionnée de mélomanes. À travers nos services, conseils et interactions, nous cherchons à créer des expériences musicales inoubliables et à accompagner nos clients tout au long de leur voyage musical.</br>
    </div>
    
</container>
    </p>

    <h2>Notre Équipe</h2>

    <p>La Bonne Note a été fondée et est dirigée par 4 passionnés de musique : VIGNAL Alexandre, DIDENKO Andriy, MONESTIER Lilian et PENET Louis.</p>
    
    <table class="team-table">
        <tr>
            <td class="team-member">
                <img src="img/lilian.jpg" alt="Lilian">
                <div>
                    <h2>Lilian</h2>
                    <p>Directeur Logistique
                    orchestre avec précision le flux des opérations pour assurer une chaîne d'approvisionnement efficace et fluide.
                    </p>
                </div>
            </td>

            <td class="team-member">
            <img src="img/alexandre.jpg" alt="Alexandre">
                <div>
                    <h2>Alexandre </h2>
                    <p>Directeur Commercial</p>
                </div>
            </td>
        </tr>
        
        <tr>
            <td class="team-member">
            <img src="img/andriy.jpg" alt="Andriy">
                <div>
                    <h2>Andriy </h2>
                    <p>Directeur Financier</p>
                </div>
            </td>

            <td class="team-member">
                <img src="img/louis.jpg" alt="Louis">
                    <div>
                        <h2>Louis </h2>
                        <p>Directeur des Ressources Humaines</p>
                    </div>
            </td>
        </tr>
    </table>
    
    

    <h2>Contact</h2>

    <p>N'hésitez pas à nous contacter si vous avez des questions ou des commentaires. </br> 
    Vous pouvez nous joindre à l'adresse LaBonneNote@gmail.com.</p>

    </div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
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
                var queryParams = $.param({
                    search: recherche,
                    category: selectedCategory
                });
                window.location.href = 'index.php?' + queryParams;
            });
        });
    </script>
    <?php include 'include/footer.php'; ?>
</body>
</html>