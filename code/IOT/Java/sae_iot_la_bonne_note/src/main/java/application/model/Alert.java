package application.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Représente une alerte en fonction des seuils définis pour la température,
 * l'humidité, l'activité et le CO2.
 * Étend la classe Data.
 */
public class Alert extends Data {

        private final Double maxTemperature;
        private final Double maxHumidity;
        private final Double maxActivity;
        private final Double maxCo2;

        /**
         * Constructeur de la classe Alert.
         *
         * @param _roomId         Identifiant de la pièce associée à l'alerte.
         * @param _date           Date de l'alerte.
         * @param _temperature    Valeur actuelle de la température.
         * @param _maxTemperature Seuil maximal de température.
         * @param _humidity       Valeur actuelle de l'humidité.
         * @param _maxHumidity    Seuil maximal d'humidité.
         * @param _activity       Valeur actuelle de l'activité.
         * @param _maxActivity    Seuil maximal d'activité.
         * @param _co2            Valeur actuelle du CO2.
         * @param _maxCo2         Seuil maximal de CO2.
         */
        public Alert(String _roomId, Date _date, double _temperature, double _maxTemperature, double _humidity,
                        double _maxHumidity, double _activity, double _maxActivity,
                        double _co2, double _maxCo2) {
                super(_roomId, _date, _temperature, null, _humidity, null, _activity, null, _co2, null);
                this.maxTemperature = _maxTemperature;
                this.maxHumidity = _maxHumidity;
                this.maxActivity = _maxActivity;
                this.maxCo2 = _maxCo2;
        }

        /**
         * Récupère le seuil maximal de température.
         *
         * @return Le seuil maximal de température.
         */
        public Double getMaxTemperature() {
                return this.maxTemperature;
        }

        /**
         * Récupère le seuil maximal d'humidité.
         *
         * @return Le seuil maximal d'humidité.
         */
        public Double getMaxHumidity() {
                return this.maxHumidity;
        }

        /**
         * Récupère le seuil maximal d'activité.
         *
         * @return Le seuil maximal d'activité.
         */
        public Double getMaxActivity() {
                return this.maxActivity;
        }

        /**
         * Récupère le seuil maximal de CO2.
         *
         * @return Le seuil maximal de CO2.
         */
        public Double getMaxCo2() {
                return this.maxCo2;
        }

        /**
         * Génère une représentation textuelle de l'alerte.
         *
         * @param _dateFormat Format de date pour la représentation textuelle.
         * @return Une chaîne de caractères représentant l'alerte.
         */
        public String toString(String _dateFormat) {
                return (" " + super.getId() + " ,") + " "
                                + (new SimpleDateFormat(_dateFormat, Locale.FRANCE).format(super.getDate()) + " ,")
                                + (super.getTemperature() != null && super.getTemperature() > maxTemperature
                                                ? " La température a dépassé le seuil ! (Valeur : "
                                                                + super.getTemperature()
                                                                + "°c & seuil maximal : "
                                                                + this.maxTemperature + "°c) ,"
                                                : "")
                                + (super.getHumidity() != null && super.getHumidity() > maxHumidity
                                                ? " L'humidité a dépassé le seuil ! (Valeur : " + super.getHumidity()
                                                                + "% & seuil maximal :  "
                                                                + this.maxHumidity + " %) ,"
                                                : "")
                                + (super.getActivity() != null && super.getActivity() > maxActivity
                                                ? " L'activité a dépassé le seuil ! (Valeur : " + super.getActivity()
                                                                + " & seuil maximal :  "
                                                                + this.maxActivity + ") ,"
                                                : "")
                                + (super.getCo2() != null && super.getCo2() > maxCo2
                                                ? " Le CO2 a dépassé le seuil ! (Valeur : " + super.getCo2()
                                                                + " ppm & seuil maximal :  "
                                                                + this.maxCo2 + " ppm) ,"
                                                : "");
        }

        /**
         * Vérifie si l'identifiant de la pièce est égal à celui de l'alerte.
         *
         * @param _idRoom L'identifiant de la pièce à vérifier.
         * @return true si l'identifiant correspond, sinon false.
         */
        public boolean Equals(String _idRoom) {
                return this.getId().equals(_idRoom);
        }
}
