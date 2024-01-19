<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Inscription - LaBonneNote</title>
<style>
  body {
    font-family: 'Arial', sans-serif;
    background-color: #f4f4f4;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
  }
  .register-container {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
    width: 300px;
  }
  .register-container h2 {
    text-align: center;
    color: #333;
    margin-bottom: 20px;
  }
  .form-control {
    margin-bottom: 10px;
  }
  .form-control label {
    display: block;
    color: #666;
    margin-bottom: 5px;
  }
  .form-control input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    box-sizing: border-box;
  }
  .form-control input[type="submit"] {
    background-color: #5cb85c;
    color: white;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  .form-control input[type="submit"]:hover {
    background-color: #4cae4c;
  }
</style>
</head>
<body>

<div class="register-container">
  <h2>Créer un compte</h2>
  <form action="TraitInscription.php" method="post">
    <div class="form-control">
      <label for="prenom">Prénom</label>
      <input type="text" id="prenom" name="prenom" required>
    </div>
    <div class="form-control">
      <label for="nom">Nom</label>
      <input type="text" id="nom" name="nom" required>
    </div>
    <div class="form-control">
      <label for="pseudo">Pseudo</label>
      <input type="text" id="pseudo" name="pseudo" required>
    </div>
    <div class="form-control">
      <label for="email">Adresse Email</label>
      <input type="email" id="email" name="email" required>
    </div>
    <div class="form-control">
      <label for="telephone">Téléphone</label>
      <input type="tel" id="telephone" name="telephone" required>
    </div>
    <div class="form-control">
      <label for="age">Date de naissance</label>
      <input type="date" id="age" name="age" required>
    </div>
    <div class="form-control">
      <label for="password">Mot de passe</label>
      <input type="password" id="password" name="password" required>
    </div>
    <div class="form-control">
      <label for="confirm-password">Confirmez le mot de passe</label>
      <input type="password" id="confirm-password" name="confirm-password" required>
    </div>
    <div class="form-control">
      <input type="submit" value="S'inscrire">
    </div>
  </form>
</div>

</body>
</html>
