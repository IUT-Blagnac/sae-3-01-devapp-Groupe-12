package application.tools;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * La classe Style fournit des méthodes pour gérer et appliquer des styles
 * personnalisés ainsi que pour manipuler des icônes dans une interface
 * utilisateur JavaFX. Elle offre des fonctionnalités pour ajouter des styles à
 * des boutons, gérer des styles d'éléments indéfinis, et définir de nouvelles
 * icônes pour les composants ImageView.
 */
public class Style {

    /**
     * Ajoute un style personnalisé au bouton spécifié.
     *
     * @param _butt     Le bouton auquel ajouter le style.
     * @param _color    La couleur de la bordure.
     * @param _fontSize La taille de la bordure.
     */
    public static void addButtonStyle(Button _butt, String _color, double _fontSize) {
        _butt.setStyle(_butt.getStyle() + "-fx-border-color: " + _color + "; -fx-border-width: " + _fontSize + ";");
    }

    /**
     * Applique un style d'élément indéfini à un nœud spécifié.
     *
     * @param _node Le nœud auquel appliquer le style.
     */
    public static void setUndefinedTextAreaStyle(Node _node) {
        _node.getStyleClass().add("undefined-txtArea");
    }

    /**
     * Réinitialise le style d'un nœud à son état par défaut.
     *
     * @param _node Le nœud dont le style doit être réinitialisé.
     */
    public static void resetTextAreaStyle(Node _node) {
        _node.getStyleClass().remove("undefined-txtArea");
    }

    /**
     * Définit une nouvelle icône pour l'ImageView spécifié.
     *
     * @param _img     L'ImageView concerné.
     * @param _imgName Le nom du fichier d'image.
     */
    public static void setNewIcon(ImageView _img, String _imgName) {
        _img.setRotate(0);
        String imagePath = "/application/images/" + _imgName;
        _img.setImage(new Image(imagePath,
                _img.getFitWidth(), _img.getFitHeight(), true,
                true));
        _img.setVisible(true);
    }

    /**
     * Configure les propriétés d'un objet Tooltip, notamment la taille de la
     * police, le délai d'affichage et la durée d'affichage.
     *
     * @param _tooltip      L'objet Tooltip à configurer.
     * @param _fontSize     La taille de la police à appliquer au texte du Tooltip,
     *                      en pixels.
     * @param _showDelay    Le délai avant l'affichage du Tooltip, spécifié en
     *                      millisecondes.
     * @param _showDuration La durée pendant laquelle le Tooltip reste affiché,
     *                      spécifiée en millisecondes.
     */
    public static void setToolTip(Tooltip _tooltip, int _fontSize, Duration _showDelay, Duration _showDuration) {
        _tooltip.setStyle("-fx-font-size: " + _fontSize + "px;");
        _tooltip.setShowDelay(_showDelay);
        _tooltip.setShowDuration(_showDuration);
    }
}