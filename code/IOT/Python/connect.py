import paho.mqtt.client as mqtt
import json
import configparser
import os
import time
from datetime import datetime
import threading

# Chemin et répertoire du fichier de configuration
config_file_path = r'code\IOT\Python\config.ini'
config_dir = os.path.dirname(config_file_path)

print("Répertoire de travail actuel :", os.getcwd())

# Lire les paramètres de configuration
config = configparser.ConfigParser()
found = config.read(config_file_path)
frequence_affichage = config.getint('MQTT', 'frequence_affichage')

print("Fichiers de configuration trouvés :", found)
print("Sections trouvées :", config.sections())

# Paramètres MQTT
broker = config.get('MQTT', 'broker')
port = config.getint('MQTT', 'port')
topic = config.get('MQTT', 'topic')
choix_donnees = config.get('MQTT', 'choix_donnees').split(',')

print("Vous avez choisi d'afficher les données suivante : ", choix_donnees)

# Dictionnaires pour stocker les valeurs et historique
values_by_room = {}
historique_par_salle = {}
file_name = os.path.join(config_dir, "donnee.txt")
pending_data = {}

def handler(signum, frame):
    # Écriture des données
    for room, data in pending_data.items():
        ecrire(room, data)

def alarm_handler():
    while True:
        time.sleep(frequence_affichage)
        handler(None, None)

alarm_thread = threading.Thread(target=alarm_handler, daemon=True)
alarm_thread.start()

def calculer_moyenne(historique):
    return sum(historique[-10:]) / min(len(historique), 10)

def ecrire(room, data):
    # Vérifier si le fichier existe, sinon le créer
    if not os.path.exists(file_name):
        open(file_name, "w").close()

    try:
        with open(file_name, "a", encoding="utf-8") as file:
            file.write(data + "\n")
    except Exception as e:
        print(f"Erreur lors de l'écriture dans le fichier : {e}")

def ecrire_alerte(alerte, room):
    alert_file = os.path.join(config_dir, "Alerte.txt")
    
    # Vérifier si le fichier existe, sinon le créer
    if not os.path.exists(alert_file):
        open(alert_file, "w").close()

    try:
        with open(alert_file, "a", encoding="utf-8") as file:
            file.write(alerte + "\n")
            
    except Exception as e:
        print(f"Erreur lors de l'écriture dans le fichier d'alerte : {e}")

def on_connect(client, userdata, flags, rc):
    print("Connecté avec le code résultat " + str(rc))
    client.subscribe(topic)

def on_message(client, userdata, msg):
    try:
        print(f"Message reçu sur le topic {msg.topic}")
        payload = json.loads(msg.payload)
        print("Payload brut:", payload)

        sensor_data = payload[0]
        device_info = payload[1]

        room = device_info['room']
        if room not in historique_par_salle:
            historique_par_salle[room] = {key: [] for key in sensor_data.keys()}

        # Obtenir la date et l'heure actuelles
        maintenant = datetime.now()
        date_heure = maintenant.strftime("%Y-%m-%d %H:%M:%S")

        alerte_texte = ""
        texte = f"Valeurs pour {room} - {date_heure}:\n"

        for key, value in sensor_data.items():
            if key.lower() in choix_donnees:
                historique_par_salle[room][key].append(value)
                moyenne = calculer_moyenne(historique_par_salle[room][key])
                texte += f"{key}: {value}, Moyenne (10 dernières): {moyenne}\n"

                seuil_key = f"seuil_{key.lower()}"
                if config.has_option('MQTT', seuil_key):
                    seuil_max = config.getint('MQTT', seuil_key)
                    if value > seuil_max:
                        alerte_texte += f"{key} a dépassé le seuil maximum de {seuil_max}. Valeur actuelle : {value}\n"

        if alerte_texte:
            ecrire_alerte(f"Alertes pour {room} - {date_heure}:\n{alerte_texte}", room)

        pending_data[room] = texte

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
