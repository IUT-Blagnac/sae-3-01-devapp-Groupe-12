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
        public Alert(String _roomId, Date _date, Double _temperature, Double _maxTemperature, Double _humidity,
                        Double _maxHumidity, Double _activity, Double _maxActivity,
                        Double _co2, Double _maxCo2) {
                super(_roomId, _date, _temperature, null, _humidity, null, _activity, null, _co2, null);
                this.maxTemperature = _maxTemperature != null ? _maxTemperature : null;
                this.maxHumidity = _maxHumidity != null ? _maxHumidity : null;
                this.maxActivity = _maxActivity != null ? _maxActivity : null;
                this.maxCo2 = _maxCo2 != null ? _maxCo2 : null;
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
         * Génère une représentation textuelle de l'alerte qui est utilisée lors des
         * notifications.
         *
         * @return Une chaîne de caractères représentant l'alerte.
         */
        @Override
        public String toString() {
                return (super.getTemperature() != null && super.getTemperature() != null && this.maxTemperature != null
                                && super.getTemperature() > maxTemperature
                                                ? "     Seuil de température dépassé !\n"
                                                : "")
                                + (super.getHumidity() != null && super.getHumidity() != null
                                                && this.maxHumidity != null
                                                && super.getHumidity() > maxHumidity
                                                                ? "     Seuil d'humidité dépassé !\n"
                                                                : "")
                                + (super.getActivity() != null && super.getActivity() != null
                                                && this.maxActivity != null
                                                && super.getActivity() > maxActivity
                                                                ? "     Seuil d'activité dépassé !\n"
                                                                : "")
                                + (super.getCo2() != null && super.getCo2() != null && this.maxCo2 != null
                                                && super.getCo2() > maxCo2
                                                                ? "     Seuil de Co2 dépassé !"
                                                                : "");
        }
}