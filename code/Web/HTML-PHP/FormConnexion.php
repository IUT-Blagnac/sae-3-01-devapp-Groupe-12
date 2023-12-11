<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Connexion - LaBonneNote</title>
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
  .login-container {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
    width: 300px;
  }
  .login-container h2 {
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

<div class="login-container">
  <h2>Connexion</h2>
  <form action="TraitConnexion.php" method="post">
    <div class="form-control">
      <label for="username">Nom d'utilisateur</label>
      <input type="text" name="username" value='<?php echo isset($_COOKIE['Cgroupe12']) ? $_COOKIE['Cgroupe12'] : ''; ?>' required><br><br>
    </div>
    <div class="form-control">
      <label for="password">Mot de passe</label>
      <input type="password" id="password" name="password" required>
    </div>
      Se souvenir de moi : <input type="checkbox" name="seSouvenirMoi"><br><br>
    <div class="form-control">
      <input type="submit" name ="connexion" value="Se connecter">
    </div>
  </form>
  <a href="Inscription.php">Créer un compte ?</a><br><br>
  <a href="MotDePasseOublie.php">Mot de passe oublié ?</a>
</div>

</body>
</html>
