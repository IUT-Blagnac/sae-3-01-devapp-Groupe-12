<footer>
    <style>
        footer {
            background-color: #333;
            color: white;
            padding: 20px;
            text-align: center;
        }

        .footer-container {
            display: flex;
            justify-content: space-around;
            flex-wrap: wrap;
        }

        .footer-section {
            margin: 20px;
            flex: 1; 
            min-width: 150px; 
        }

        .footer-section h4 {
            margin-bottom: 10px;
        }

        .footer-section p, .footer-section a, .footer-section input[type="email"], .footer-section button {
            margin-bottom: 5px;
            color: white;
            text-decoration: none;
        }

        .footer-section input[type="email"], .footer-section button {
            padding: 10px;
            border: none;
            margin-top: 5px;
        }

        .footer-section button {
            background-color: #0082e6;
            color: white;
            cursor: pointer;
        }

        .social-icons {
            display: flex; 
            flex-direction: column; 
            align-items: center; 
        }

        .social-icons a {
            margin-bottom: 10px; 
        }

        .social-icons img {
            height: 20px; 
            width: 20px;
        }

        @media (max-width: 768px) {
            .footer-container {
                flex-direction: column;
                align-items: center;
            }

            .footer-section {
                margin: 10px 0; 
            }

            .social-icons {
                flex-direction: row; 
                justify-content: center;
            }

            .social-icons a {
                margin: 0 5px; 
            }

            .legal ul {
            list-style-type: none;
            padding: 0; 
            margin: 0;
        }

        .legal li {
            margin-bottom: 5px;
        }

        .legal a {
            color: white;
            text-decoration: none;
        }

        .legal a:hover {
            text-decoration: underline;
        }
        }
    </style>
    <div class="footer-container">
        <div class="footer-section contact-info">
            <h4>Contactez-nous</h4>
            <p>123 Rue de la Musique, 31300 Toulouse</p>
            <p>Téléphone : <a href="tel:+33749283008" style="color: inherit; text-decoration: none;">07 49 28 30 08</a></p>
            <p>Horaires : Lun - Sam, 6h - 23h</p>
        </div>
        <div class="footer-section links">
            <h4>Liens utiles</h4>
            <a href="/politique-retour">Politique de retour</a>
            <a href="/faq">FAQ</a>
            <a href="/guides">Guides d'achat</a>
            <a href="/plan-site">Plan du site</a>
        </div>
        <div class="footer-section social-icons">
            <h4>Suivez-nous</h4>
             <a href="#"><img src="img/facebook.png" alt="Facebook"></a>
            <a href="#"><img src="img/twitter.png" alt="X"></a>
            <a href="#"><img src="img/instagram.png" alt="Instagram"></a>
            <a href="#"><img src="img/youtube.png" alt="YouTube"></a>
        </div>
        <div class="footer-section newsletter">
            <h4>Newsletter</h4>
            <input type="email" placeholder="Entrez votre email">
            <button type="submit">S'abonner</button>
        </div>
        <div class="footer-section legal">
            <h4>Informations légales</h4>
            <ul>
                <li><a href="/cookies">Cookies</a></li>
                <li><a href="/conditions-generales-ventes">Conditions générales de vente</a></li>
                <li><a href="/protection-donnees">Protection des données</a></li>
                <li><a href="/mentions-legales">Mentions légales</a></li>
            </ul>
        </div>
    </div>
</footer>
