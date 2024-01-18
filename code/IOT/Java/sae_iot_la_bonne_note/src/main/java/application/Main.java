package application;

import application.control.MainMenu;

/**
 * La classe Main contient la méthode principale de l'application. Elle est
 * responsable du démarrage de l'application.
 * Cette classe est utilisée pour lancer le programme et exécuter la méthode
 * principale qui initialise et démarre l'application.
 */
public class Main {

    /**
     * La méthode principale de l'application.
     *
     * @param args Les arguments de la ligne de commande (non utilisés dans cette
     *             application).
     */
    public static void main(String[] args) {
        MainMenu.runApp();
    }
}