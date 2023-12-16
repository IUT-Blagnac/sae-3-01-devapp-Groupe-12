package application.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Data {
    private final String id;
    private final Date date;
    private final Double temperature;
    private final Double humidity;
    private final Double activity;
    private final Double co2;

    public Data(String _roomId, Date _date, Double _temperature, Double _humidity, Double _activity,
            Double _co2) {
        this.id = _roomId;
        this.date = _date;
        this.temperature = _temperature == null ? null : _temperature;
        this.humidity = _humidity == null ? null : _humidity;
        this.activity = _activity == null ? null : _activity;
        this.co2 = _co2 == null ? null : _co2;
    }

    public String getId() {
        return this.id;
    }

    public Date getDate() {
        return this.date;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public Double getHumidity() {
        return this.humidity;
    }

    public Double getActivity() {
        return this.activity;
    }

    public Double getCo2() {
        return this.co2;
    }

    public String toString(String _dateFormat) {
        return (" " + id + ",") + (" " + new SimpleDateFormat(_dateFormat, Locale.FRANCE).format(date) + ",")
                + (temperature != null ? " Température : " + temperature + "°c ," : "")
                + (humidity != null ? " Humidité : " + humidity + "% ," : "")
                + (activity != null ? " Activité : " + activity + " ," : "")
                + (co2 != null ? " Co2 : " + co2 + "ppm ": "");
    }
}
