package application.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Représente les données associées à une mesure spécifique.
 */
public class Data {

    private final String id;
    private final Date date;

    private final Double temperature;
    private final Double avgTemperature;

    private final Double humidity;
    private final Double avgHumidity;

    private final Double activity;
    private final Double avgActivity;

    private final Double co2;
    private final Double avgCo2;

    /**
     * Constructeur de la classe Data.
     *
     * @param _roomId         Identifiant de la pièce associée aux données.
     * @param _date           Date des données.
     * @param _temperature    Valeur de température.
     * @param _avgTemperature Moyenne de température.
     * @param _humidity       Valeur d'humidité.
     * @param _avgHumidity    Moyenne d'humidité.
     * @param _activity       Valeur d'activité.
     * @param _avgActivity    Moyenne d'activité.
     * @param _co2            Valeur de CO2.
     * @param _avgCo2         Moyenne de CO2.
     */
    public Data(String _roomId, Date _date, Double _temperature, Double _avgTemperature, Double _humidity,
            Double _avgHumidity, Double _activity, Double _avgActivity, Double _co2, Double _avgCo2) {
        this.id = _roomId;
        this.date = _date;
        this.temperature = _temperature;
        this.avgTemperature = _avgTemperature;
        this.humidity = _humidity;
        this.avgHumidity = _avgHumidity;
        this.activity = _activity;
        this.avgActivity = _avgActivity;
        this.co2 = _co2;
        this.avgCo2 = _avgCo2;
    }

    /**
     * Récupère l'identifiant de la pièce.
     *
     * @return L'identifiant de la pièce.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Récupère la date des données.
     *
     * @return La date des données.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Récupère la valeur de température.
     *
     * @return La valeur de température.
     */
    public Double getTemperature() {
        return this.temperature;
    }

    /**
     * Récupère la moyenne de température.
     *
     * @return La moyenne de température.
     */
    public Double getAvgTemperature() {
        return this.avgTemperature;
    }

    /**
     * Récupère la valeur d'humidité.
     *
     * @return La valeur d'humidité.
     */
    public Double getHumidity() {
        return this.humidity;
    }

    /**
     * Récupère la moyenne d'humidité.
     *
     * @return La moyenne d'humidité.
     */
    public Double getAvgHumidity() {
        return this.avgHumidity;
    }

    /**
     * Récupère la valeur d'activité.
     *
     * @return La valeur d'activité.
     */
    public Double getActivity() {
        return this.activity;
    }

    /**
     * Récupère la moyenne d'activité.
     *
     * @return La moyenne d'activité.
     */
    public Double getAvgActivity() {
        return this.avgActivity;
    }

    /**
     * Récupère la valeur de CO2.
     *
     * @return La valeur de CO2.
     */
    public Double getCo2() {
        return this.co2;
    }

    /**
     * Récupère la moyenne de CO2.
     *
     * @return La moyenne de CO2.
     */
    public Double getAvgCo2() {
        return this.avgCo2;
    }

    /**
     * Génère une représentation textuelle des données.
     *
     * @param _dateFormat Format de date pour la représentation textuelle.
     * @return Une chaîne de caractères représentant les données.
     */
    public String toString(String _dateFormat) {
        return (" " + id + ",") + (" " + new SimpleDateFormat(_dateFormat, Locale.FRANCE).format(date) + ",")
                + (temperature != null ? " Température : " + temperature + "°c ," : "")
                + (humidity != null ? " Humidité : " + humidity + "% ," : "")
                + (activity != null ? " Activité : " + activity + " ," : "")
                + (co2 != null ? " CO2 : " + co2 + "ppm " : "");
    }

    /**
     * Permet de comparer si deux données sont identiques à travers leurs
     * identifiant et la data.
     *
     * @param _id   Identifiant de la donnée à comparer.
     * @param _date Date de la donnée à comparer.
     * @return True si l'identifiant et la date sont les mêmes, false sinon.
     */
    public boolean equals(String _id, Date _date) {
        return this.id.equals(_id) && this.date.equals(_date);
    }
}