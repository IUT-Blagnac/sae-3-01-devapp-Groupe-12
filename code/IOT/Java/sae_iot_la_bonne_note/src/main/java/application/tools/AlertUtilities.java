package application.tools;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Classe utilitaire pour la gestion des boîtes de dialogue d'alerte.
 * Cette classe fournit des méthodes pour afficher différents types d'alertes.
 */
public class AlertUtilities {

	/**
	 * Affiche une boîte de dialogue de confirmation avec les boutons "Oui" et
	 * "Annuler".
	 * 
	 * @param _fen     Le stage de la fenêtre propriétaire de la boîte de dialogue.
	 * @param _title   Le titre de la boîte de dialogue.
	 * @param _message Le message d'en-tête de la boîte de dialogue.
	 * @param _content Le contenu de la boîte de dialogue.
	 * @param _at      Le type d'alerte (par défaut INFORMATION si non spécifié).
	 * @return true si le bouton "Oui" est sélectionné, sinon false.
	 */
	public static boolean confirmYesCancel(Stage _fen, String _title, String _message, String _content, AlertType _at) {

		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals(""))
			alert.setHeaderText(_message);
		alert.setContentText(_content);

		Optional<ButtonType> option = alert.showAndWait();
		if (option.isPresent() && option.get() == ButtonType.OK) {
			return true;
		}
		return false;
	}

	/**
	 * Affiche une boîte de dialogue d'alerte.
	 * 
	 * @param _fen     Le stage de la fenêtre propriétaire de la boîte de dialogue.
	 * @param _title   Le titre de la boîte de dialogue.
	 * @param _message Le message d'en-tête de la boîte de dialogue.
	 * @param _content Le contenu de la boîte de dialogue.
	 * @param _at      Le type d'alerte (par défaut INFORMATION si non spécifié).
	 */
	public static void showAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {

		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals(""))
			alert.setHeaderText(_message);
		alert.setContentText(_content);

		alert.showAndWait();
	}

	/**
	 * Affiche une boîte de dialogue d'alerte sans spécifier le stage propriétaire.
	 * 
	 * @param _title   Le titre de la boîte de dialogue.
	 * @param _message Le message d'en-tête de la boîte de dialogue.
	 * @param _at      Le type d'alerte (par défaut INFORMATION si non spécifié).
	 */
	public static void showAlert(String _title, String _message, AlertType _at) {

		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.setTitle(_title);
		if (_message == null || !_message.equals(""))
			alert.setHeaderText(_message);

		alert.showAndWait();
	}
}
