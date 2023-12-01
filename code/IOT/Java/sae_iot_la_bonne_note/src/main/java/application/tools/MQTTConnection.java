package application.tools;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Classe utilitaire pour la gestion des connexions MQTT.
 */
public class MQTTConnection {

    /**
     * Teste la connexion au serveur MQTT à l'aide de l'hôte et du port spécifiés.
     *
     * @param host L'hôte du serveur MQTT.
     * @param port Le port du serveur MQTT.
     * @return true si la connexion est établie avec succès, false sinon.
     */
    public static boolean testMQTTConnection(String host, int port) {
        // Formatte l'adresse du broker MQTT
        String broker = String.format("tcp://%s:%d", host, port);

        // Génère un identifiant de client MQTT
        String clientId = MqttClient.generateClientId();

        try {
            // Initialise un client MQTT
            IMqttClient mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            connOpts.setConnectionTimeout(10); // Définit le timeout de connexion à 10 secondes

            // Tente de se connecter au broker MQTT
            mqttClient.connect(connOpts);

            // Déconnecte et ferme la connexion MQTT
            mqttClient.disconnect();
            mqttClient.close();

            return true; // La connexion a réussi
        } catch (Exception e) {
            return false; // La connexion a échoué
        }
    }
}
