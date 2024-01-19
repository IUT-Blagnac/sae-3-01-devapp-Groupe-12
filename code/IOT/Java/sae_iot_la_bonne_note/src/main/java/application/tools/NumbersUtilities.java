package application.tools;

/**
 * Classe utilitaire pour la conversion des chaînes en nombres.
 */
public class NumbersUtilities {
    /**
     * Convertit une chaîne en entier.
     * 
     * @param _string La chaîne à convertir en entier.
     * @return La valeur entière de la chaîne si la conversion réussit, sinon
     *         retourne 0.
     */
    public static int getIntFromString(String _string) {
        try {
            int val = Integer.parseInt(_string);
            return val;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Convertit une chaîne en Double.
     * 
     * @param _string La chaîne à convertir en Double.
     * @return La valeur entière de la chaîne si la conversion réussit, sinon
     *         retourne 0.0.
     */
    public static Double getDoubleFromString(String _string) {
        try {
            Double val = Double.parseDouble(_string);
            return val;
        } catch (Exception e) {
            return null;
        }
    }
}
