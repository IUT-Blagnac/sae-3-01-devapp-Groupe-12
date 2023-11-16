import paho.mqtt.client as mqtt
import json
import configparser
import os
import time
from datetime import datetime

# Chemin du fichier de configuration
config_file_path = r'code\IOT\Python\config.ini'

# Obtenir le répertoire du fichier de configuration
config_dir = os.path.dirname(config_file_path)


print("Répertoire de travail actuel :", os.getcwd())

# Lire les paramètres de configuration
config = configparser.ConfigParser()
found = config.read(config_file_path)

frequence_affichage = config.getint('MQTT', 'frequence_affichage')
print("Fichiers de configuration trouvés :", found)
print("Sections trouvées :", config.sections())

broker = config.get('MQTT', 'broker')
port = config.getint('MQTT', 'port')
topic = config.get('MQTT', 'topic')
choixDonnees = config.get('MQTT', 'choixDonnees').split(',')  # Récupère les valeurs à afficher
print("Vous avez choisi d'afficher les données suivante : ", choixDonnees)

# Dictionnaire pour stocker les valeurs
values_by_room = {}
historique_par_salle = {}

# Nom du fichier pour écrire les données
file_name = os.path.join(config_dir, "donnee.txt")

# Supprimer un fichier existant
if os.path.exists(file_name):
    os.remove(file_name)

# Fonction pour calculer la moyenne des 10 dernières valeurs
def calculer_moyenne(historique):
    return sum(historique[-10:]) / min(len(historique), 10)

# Fonction pour écrire dans un fichier
def ecrire(room, data):
    try:
        with open(file_name, "a", encoding="utf-8") as file:
            if room not in values_by_room:
                file.write(f"\nSalle : {room}\n")
                values_by_room[room] = []  # Initialise la liste pour la nouvelle salle
            file.write(data + "\n")
        time.sleep(frequence_affichage)  # Attendre avant d'afficher la prochaine salle
    except Exception as e:
        print(f"Erreur lors de l'écriture dans le fichier : {e}")

# Fonction appelée lors de la connexion au broker
def on_connect(client, userdata, flags, rc):
    print("Connecté avec le code résultat " + str(rc))
    client.subscribe(topic)

# Fonction appelée à la réception d'un message
def on_message(client, userdata, msg):
    try:
        print(f"Message reçu sur le topic {msg.topic}")
        payload = json.loads(msg.payload)
        print("Payload brut:", payload)

        # Traitement des données
        sensor_data = payload[0]
        device_info = payload[1]

        room = device_info['room']
        if room not in historique_par_salle:
            historique_par_salle[room] = {key: [] for key in sensor_data.keys()}

        # Mise à jour des historiques et calcul des moyennes
        texte = f"Valeurs pour {room}:\n"
        for key, value in sensor_data.items():
            if key.lower() in choixDonnees:  # Vérifie si la clé est dans les valeurs à afficher
                historique_par_salle[room][key].append(value)
                # S'assurer que la liste ne contient que les 10 dernières valeurs
                if len(historique_par_salle[room][key]) > 10:
                    historique_par_salle[room][key].pop(0)  # Supprime la valeur la plus ancienne
                moyenne = calculer_moyenne(historique_par_salle[room][key])
                texte += f"{key}: {value}, Moyenne (10 dernières): {moyenne}\n"

        print(texte)
        ecrire(room, texte)

    except Exception as e:
        print(f"Erreur lors du traitement du message: {e}")

# Création et configuration du client MQTT
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

# Connexion au broker
client.connect(broker, port, 60)

# Boucle de traitement des messages
client.loop_forever()