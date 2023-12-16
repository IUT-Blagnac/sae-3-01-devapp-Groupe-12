package application.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alert extends Data {
        private final Double maxTemperature;
        private final Double maxHumidity;
        private final Double maxActivity;
        private final Double maxCo2;

        public Alert(String _roomId, Date _date, double _temperature, double _maxTemperature, double _humidity,
                        double _maxHumidity, double _activity, double _maxActivity,
                        double _co2, double _maxCo2) {
                super(_roomId, _date, _temperature, _humidity, _activity, _co2);
                this.maxTemperature = _maxTemperature;
                this.maxHumidity = _maxHumidity;
                this.maxActivity = _maxActivity;
                this.maxCo2 = _maxCo2;
        }

        public Double getMaxTemperature() {
                return this.maxTemperature;
        }

        public Double getMaxHumidity() {
                return this.maxHumidity;
        }

        public Double getMaxActivity() {
                return this.maxActivity;
        }

        public Double getMaxCo2() {
                return this.maxCo2;
        }

        public String toString(String _dateFormat) {
                return (" " + super.getId() + " ,") + " "
                                + (new SimpleDateFormat(_dateFormat, Locale.FRANCE).format(super.getDate()) + " ,")
                                + (super.getTemperature() != null && super.getTemperature() > maxTemperature
                                                ? " La température a dépassé le seuil ! (Valeur : "
                                                                + super.getTemperature()
                                                                + "°c & seuil : "
                                                                + this.maxTemperature + "°c) ,"
                                                : "")
                                + (super.getHumidity() != null && super.getHumidity() > maxHumidity
                                                ? " L'humidité a dépassé le seuil ! (Valeur : " + super.getHumidity()
                                                                + "% & seuil :  "
                                                                + this.maxHumidity + " %) ,"
                                                : "")
                                + (super.getActivity() != null && super.getActivity() > maxActivity
                                                ? " L'activité a dépassé le seuil ! (Valeur : " + super.getActivity()
                                                                + " & seuil :  "
                                                                + this.maxActivity + ") ,"
                                                : "")
                                + (super.getCo2() != null && super.getCo2() > maxCo2
                                                ? " Le co2 a dépassé le seuil ! (Valeur : " + super.getCo2()
                                                                + " ppm & seuil :  "
                                                                + this.maxCo2 + " ppm) ,"
                                                : "");
        }
}