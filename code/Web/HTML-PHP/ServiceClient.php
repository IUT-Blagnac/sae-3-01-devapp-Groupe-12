<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    echo "<script>
    alert(\"Vous devez être connecté pour accéder au service client. Vous allez être redirigé vers la page d\'accueil.\");
    window.location.href = 'index.php'</script>";
}
$recherche = isset($_GET['search']) ? $_GET['search'] : null;

$selectedCategory = isset($_GET['category']) ? $_GET['category'] : 'Tous nos produits';
?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <title>Service Client</title>
    <link rel="stylesheet" href="Css/ServiceClient.css">
</head>

<body>
    <?php include 'include/header.php'; ?>
    <div class="contact-options">
        <h2>Nous contacter</h2>
        <div class="content-container">
            <img src="img/service.png" alt="Image du service client" class="contact-image">
            <div class="button-container">
                <a class="contact-button" href="mailto:guychelbabela@gmail.com">Mail</a>
                <a class="contact-button" href="tel:+33749283008">Téléphone</a>
                <a class="contact-button" href="#" onclick="toggleChat()">Chat</a>
            </div>
            <div class="chat-scene" id="chat-scene">
                <div class="chat-window" id="chat-window">
                    <div class="messages" id="messages">
                    </div>
                </div>
                <div class="input-area">
                    <input type="text" id="messageInput" placeholder="Votre message...">
                    <button onclick="sendMessage()">Envoyer</button>
                    <div id="loading" class="loading-animation" style="display: none;">
                        <!-- <img src="img/loadingIcon.png"> -->
                    </div>
                </div>
            </div>
            <div class="spacer"></div>
        </div>
    </div>

    <script type="text/javascript">
        function toggleChat() {
            var chatScene = document.getElementById('chat-scene');
            chatScene.style.display = 'block';
        }

        function sendMessage() {
            var messageInput = document.getElementById('messageInput');
            var message = messageInput.value.trim();

            if (message !== '') {
                var messagesDiv = document.getElementById('messages');
                messagesDiv.innerHTML += `<div class="sent-message">${message}</div>`;

                // Afficher l'animation de chargement avant l'appel à l'API
                var loadingDiv = document.getElementById('loading');
                loadingDiv.style.display = 'block';

                callOpenAI(message)
                    .then(response => {
                        // Cacher l'animation une fois que la réponse est reçue
                        loadingDiv.style.display = 'none';

                        messagesDiv.innerHTML += `<div class="sent-message">${response}</div>`;
                    })
                    .catch(error => {
                        console.error('Erreur :', error);
                        loadingDiv.style.display = 'none'; // Cacher l'animation en cas d'erreur
                    });
            }
            messageInput.value = '';
        }



        function callOpenAI(message) {
            const openai_endpoint = "https://api.openai.com/v1/chat/completions";
            const openai_token = "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";

            const data = {
                model: "gpt-3.5-turbo",
                messages: [{
                        role: "system",
                        content: "Tu travailles dans un service client d'un site de vente d'instruments de musique, le magasin se nomme \"La Bonne Note\", tu dois donc répondre aux demandes des clients. Tu as la capacité de rediriger les clients vers la page d'accueil (retour vers index.php)."
                    },
                    {
                        role: "user",
                        content: message
                    }
                ],
                max_tokens: 100,
                temperature: 0.7
            };

            const headers = {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${openai_token}`
            };

            return fetch(openai_endpoint, {
                    method: "POST",
                    headers: headers,
                    body: JSON.stringify(data)
                })
                .then(response => response.json())
                .then(data => {
                    return data.choices[0].message.content;
                })
                .catch(error => {
                    console.error('Erreur :', error);
                    throw error;
                });
        }
    </script>
    <div id="chat-window" style="display: block;">
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
<!DOCTYPE html>
<html lang="fr">