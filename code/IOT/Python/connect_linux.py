import paho.mqtt.client as mqtt
import json
import configparser
import os
from datetime import datetime
import signal

# Chemin du fichier de configuration
config_file_path = r'config.ini'

# Obtenir le répertoire du fichier de configuration
config_dir = os.path.dirname(config_file_path)

print("Répertoire de travail actuel :", os.getcwd())

# Lire les paramètres de configuration
config = configparser.ConfigParser()
found = config.read(config_file_path)

# Récupèrer la fréquence d'affichage à partir des paramètres de configuration MQTT
frequence_affichage = config.getint('CONFIG', 'frequence_affichage') 

print("Fichiers de configuration trouvés :", found)
print("Sections trouvées :", config.sections())

broker = config.get('MQTT', 'broker')
port = config.getint('MQTT', 'port')
topic = config.get('MQTT', 'topic')
choix_donnees = config.get('CONFIG', 'choix_donnees').split(',')  # Récupère les valeurs à afficher
print("Vous avez choisi d'afficher les données suivante : ", choix_donnees)

# Dictionnaire pour stocker les valeurs
values_by_room = {}
historique_par_salle = {}

# Dictionnaire pour stocker les valeurs
values_by_room = {}
historique_par_salle = {}

# Nom du fichier pour écrire les logs
fichier_logs = os.path.join(config_dir, config.get('CONFIG', 'fichier_logs') + '.json')

# Nom du fichier pour écrire les données
fichier_donnees = os.path.join(config_dir, config.get('CONFIG', 'fichier_donnees') + '.json')

# Nom du fichier pour écrire les alertes
fichier_alertes = os.path.join(config_dir, config.get('CONFIG', 'fichier_alerte') + '.json')

# Supprimer le fichier existant
if os.path.exists(fichier_donnees):
    os.remove(fichier_donnees)

# Dictionnaire permettant de stocker les données en attente d'écriture
pending_data = {}


# On appel cette fonction lors de la réception du signal d'alarme
def handler(signum, frame):
    # Parcours les données en attente et les écrit dans le fichier
    # for room, data in pending_data.items():
    #     ecrire(room, data)
    # Programmation d'une nouvelle alarme pour la prochaine écriture
    signal.alarm(frequence_affichage)


# Association du handler au signal d'alarme
signal.signal(signal.SIGALRM, handler)


# Fonction pour calculer la moyenne des 10 dernières valeurs
def calculer_moyenne(historique):
    return sum(historique[-10:]) / min(len(historique), 10)


def ecrire(ecrire_log, nom_fichier, room, data):
    try:
        # Ouverture du fichier JSON en mode Lecture / Ecriture, le créer s'il n'existe pas avec les droits 644(User rw, Group rx, Others rx)
        fichier = os.open(nom_fichier, os.O_RDWR | os.O_CREAT, 0o644)
        
        # Écriture d'un objet JSON vide si le fichier est nouveau
        if os.path.getsize(fichier) == 0:
            os.write(fichier, json.dumps({}).encode())
        
        # Lecture des données JSON existantes
        os.lseek(fichier, 0, os.SEEK_SET)
        contenu = os.read(fichier, os.path.getsize(fichier)).decode()
        if contenu:
            donnees = json.loads(contenu)
        else:
            donnees = {}
        # Mise à jour des données avec les nouvelles informations
        if room not in donnees:
            donnees[room] = []
        # Si écriture dans le fichier de log, on ajoute les données au lieu de les remplacer
        if(ecrire_log):
            donnees[room].append(data)
        else: 
            donnees[room] = data
        # Écriture des données mises à jour dans le fichier sans effacer le contenu existant
        os.lseek(fichier, 0, os.SEEK_SET)
        os.write(fichier, json.dumps(donnees, indent=4).encode())
        # Fermeture du descripteur de fichier
        os.close(fichier)
    except Exception as e:
        print(f"Erreur lors de l'écriture dans le fichier données : {e}")


def ecrire_alerte(room, alerte):
    try:
        # Ouverture du fichier JSON en mode Lecture / Ecriture, le créer s'il n'existe pas avec les droits 644(User rw, Group rx, Others rx)
        fic_alertes = os.open(fichier_alertes, os.O_RDWR | os.O_CREAT, 0o644)
        
        # Écriture d'un objet JSON vide si le fichier est nouveau
        if os.path.getsize(fichier_alertes) == 0:
            os.write(fic_alertes, json.dumps({}).encode())
        
        # Lecture des données JSON existantes
        os.lseek(fic_alertes, 0, os.SEEK_SET)
        contenu = os.read(fic_alertes, os.path.getsize(fichier_alertes)).decode()
        if contenu:
            donnees = json.loads(contenu)
        else:
            donnees = {}
        # Mise à jour des données avec les nouvelles informations
        if room not in donnees:
            donnees[room] = []
        donnees[room].append(alerte)

        os.lseek(fic_alertes, 0, os.SEEK_SET)
        os.write(fic_alertes, json.dumps(donnees, indent=4).encode())
        # Fermeture du descripteur de fichier
        os.close(fic_alertes)
    except Exception as e:
        print(f"Erreur lors de l'écriture dans le fichier d'alerte : {e}")

        
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

        maintenant = datetime.now()
        date_heure = maintenant.strftime("%d-%m-%Y %H:%M:%S")  

        envoyer_alerte = False
        
        alerte = {
            "date": date_heure,
            "donnees": {}
            }
        alerte_texte = ""

        salle_donnees = {
            "date": date_heure,
            "donnees": {}
            }

        donnees_logs = {
            "date": date_heure,
            "donnees": {}
            }

        for key, value in sensor_data.items():
            if key.lower() in choix_donnees:  # Vérifie si la clé est dans les valeurs à afficher
                historique_par_salle[room][key].append(value)
                # S'assurer que la liste ne contient que les 10 dernières valeurs
                if len(historique_par_salle[room][key]) > 10:
                    historique_par_salle[room][key].pop(0)  # Supprime la valeur la plus ancienne
                moyenne = calculer_moyenne(historique_par_salle[room][key])
                salle_donnees["donnees"][key] = {
                    "valeur": value,
                    "moyenne": moyenne
                }
                donnees_logs["donnees"][key] = value
                # Vérification des seuils pour déclencher une alerte
                seuil_key = f"seuil_{key.lower()}"
                if config.has_option('ALERT', seuil_key):
                    seuil_max = config.getint('ALERT', seuil_key)
                    if value > seuil_max:
                        alerte["donnees"][key] = {
                            "valeur": value,
                            "seuil_max": seuil_max
                            }
                        alerte_texte += f"- {key} a dépassé le seuil maximum de {seuil_max}, valeur actuelle : {value}\n"
                        envoyer_alerte = True
        print(salle_donnees)
        if(envoyer_alerte):
            print ("Alertes : \n" + alerte_texte)
            ecrire_alerte(room, alerte)  # S'il y a des alertes, les écrire dans le fichier
        ecrire(False, fichier_donnees, room, salle_donnees)
        ecrire(True, fichier_logs, room, salle_donnees)

        #pending_data[room] = salle_donnees
        signal.alarm(0)
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
