package application.tools;

import org.controlsfx.control.Notifications;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ListViewUtilies {

    private static boolean isTemperatureSelected;
    private static boolean isHumiditySelected;
    private static boolean isActivitySelected;
    private static boolean isCo2Selected;

    public static void updateSelectedElements(boolean _temperature, boolean _humidity, boolean _activity, boolean co2) {
        ListViewUtilies.isTemperatureSelected = _temperature;
        ListViewUtilies.isHumiditySelected = _humidity;
        ListViewUtilies.isActivitySelected = _activity;
        ListViewUtilies.isCo2Selected = co2;
    }

    public static void setCellForAlert(ListView<String> _listView) {
        _listView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] data = item.toString().split(",");
                    if (data.length >= 2) {
                        String room = data[0];
                        String date = data[1];

                        VBox infoVBox = new VBox();
                        infoVBox.setSpacing(10);

                        Label roomLabel = new Label(" Salle : " + room);
                        roomLabel.getStyleClass().add("labelRoom");
                        Label dateLabel = new Label(" Date : " + date);
                        dateLabel.getStyleClass().add("labelDate");

                        ImageView imageView = new ImageView(new Image("/application/images/alert_data_icon.png"));
                        imageView.setFitWidth(80);
                        imageView.setFitHeight(80);
                        imageView.getStyleClass().add("centered-image");

                        infoVBox.setAlignment(Pos.CENTER);
                        infoVBox.getChildren().addAll(roomLabel, dateLabel, imageView);

                        VBox dataVBox = new VBox();
                        dataVBox.setSpacing(10);

                        for (int i = 2; i < data.length; i++) {
                            String element = data[i];
                            if (isElementSelected(element)) {
                                Label dataLabel = new Label(element);
                                if (element.contains("Date")) {
                                    dataLabel.getStyleClass().add("labelDate");
                                } else if (element.toLowerCase().contains("température")) {
                                    dataLabel.getStyleClass().add("labelTemperature");
                                } else if (element.toLowerCase().contains("humidité")) {
                                    dataLabel.getStyleClass().add("labelHumidity");
                                } else if (element.toLowerCase().contains("activité")) {
                                    dataLabel.getStyleClass().add("labelActivity");
                                } else if (element.toLowerCase().contains("co2")) {
                                    dataLabel.getStyleClass().add("labelCo2");
                                }
                                dataVBox.getChildren().add(dataLabel);
                            }
                        }

                        HBox hbox = new HBox();
                        hbox.setSpacing(20);
                        hbox.setAlignment(Pos.CENTER);

                        hbox.getChildren().addAll(infoVBox, dataVBox);

                        setGraphic(hbox);
                    }
                }
            }
        });
    }

    public static void setCellForData(ListView<String> _listView) {
        _listView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] data = item.toString().split(",");
                    if (data.length >= 2) {
                        HBox hbox = new HBox();
                        hbox.setSpacing(5);

                        Label label = new Label(data[0] + " ");
                        label.getStyleClass().add("labelRoom");
                        hbox.getChildren().add(label);
                        label = new Label(data[1] + " ");
                        label.getStyleClass().add("labelDate");
                        hbox.getChildren().add(label);

                        for (int i = 2; i < data.length; i++) {
                            String element = data[i];
                            if (isElementSelected(element.toLowerCase())) {
                                label = new Label(element);
                                if (element.contains("Date")) {
                                    label.getStyleClass().add("labelDate");
                                } else if (element.toLowerCase().contains("température")) {
                                    label.getStyleClass().add("labelTemperature");
                                } else if (element.toLowerCase().contains("humidité")) {
                                    label.getStyleClass().add("labelHumidity");
                                } else if (element.toLowerCase().contains("activité")) {
                                    label.getStyleClass().add("labelActivity");
                                } else if (element.toLowerCase().contains("co2")) {
                                    label.getStyleClass().add("labelCo2");
                                }
                                hbox.getChildren().add(label);
                            }
                            setGraphic(hbox);
                        }
                    }
                }
            }
        });
    }

    private static boolean isElementSelected(String element) {
        if (element.contains("température")) {
            return isTemperatureSelected;
        } else if (element.contains("humidité")) {
            return isHumiditySelected;
        } else if (element.contains("activité")) {
            return isActivitySelected;
        } else if (element.contains("co2")) {
            return isCo2Selected;
        }
        return true;
    }
}
