package application.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fournit des méthodes utilitaires pour manipuler les dates et les formats de
 * date.
 */
public class DateUtilities {

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);

    /**
     * Convertit une chaîne de caractères représentant une date en objet Date.
     *
     * @param _dateString La chaîne de caractères représentant la date.
     * @return L'objet Date correspondant à la chaîne spécifiée, ou null en cas
     *         d'erreur.
     */
    public static Date getDateFromString(String _dateString) {
        try {
            return inputFormat.parse(_dateString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Transforme un format de date personnalisé pour correspondre aux normes de
     * SimpleDateFormat.
     *
     * @param _dateFormat Le format de date à transformer.
     * @return Le format de date transformé pour être utilisé avec SimpleDateFormat.
     */
    public static String transformDateFormat(String _dateFormat) {
        _dateFormat = _dateFormat.replace("JJ", "dd");
        _dateFormat = _dateFormat.replace("AAAA", "yyyy");
        _dateFormat = _dateFormat.replace("hh", "HH'h'");
        return _dateFormat;
    }
}