package application.tools;

import java.io.IOException;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Cette classe gère le démarrage, l'arrêt et la surveillance des processus
 * Python et la mise à jour des données via des threads.
 * Elle permet également de gérer les images d'état de connexion.
 */
public class PythonAndThreadManagement {

    private static Thread pythonThread;
    private static Process pythonProcess;
    private static ImageView imgConnexionState;

    /**
     * Initialise l'image représentant l'état de la connexion.
     *
     * @param _imgConnexionState L'imageView pour l'état de la connexion.
     */
    public static void initImgConnexionState(ImageView _imgConnexionState) {
        imgConnexionState = _imgConnexionState;
    }

    /**
     * Met à jour l'image représentant l'état de la connexion en fonction de
     * l'exécution de Python.
     */
    public static void updateImgConnexionState() {
        Platform.runLater(() -> {
            if (isPythonRunning()) {
                Style.setNewIcon(imgConnexionState, "connexion_icon.png");
                Animations.startConnectedAnimation(imgConnexionState);
            } else {
                Style.setNewIcon(imgConnexionState, "connection_fail.png");
            }
        });
    }

    /**
     * Démarre un nouveau thread pour exécuter le script Python.
     *
     * @param _primaryStage La fenêtre principale de l'application.
     */
    public static void startPythonThread(Stage _primaryStage) {
        pythonThread = new Thread(() -> {
            String scriptPath = "connect.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
            try {
                pythonProcess = processBuilder.start();
                Style.setNewIcon(imgConnexionState, "connexion_icon.png");
                Animations.startConnectedAnimation(imgConnexionState);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Style.setNewIcon(imgConnexionState, "connection_fail.png");
                    AlertUtilities.showAlert(_primaryStage, "Erreur",
                            "Lancement du script Python impossible.",
                            "Une erreur est survenue lors du lancement du script Python."
                                    + ".\nCode d'erreur : " + e,
                            AlertType.ERROR);
                });
            }
        });
        pythonThread.start();
    }

    /**
     * Vérifie si le processus Python est en cours d'exécution.
     *
     * @return true si le processus Python est en cours d'exécution, sinon false.
     */
    public static boolean isPythonRunning() {
        return pythonProcess != null && pythonProcess.isAlive();
    }

    /**
     * Arrête le thread Python en cours.
     */
    public static void stopPythonThread() {
        if (isPythonRunning()) {
            pythonProcess.destroy();
            JsonReader.deleteHistory("fichier_donnees");
        }
        if (pythonThread != null && pythonThread.isAlive()) {
            pythonThread.interrupt();
        }
    }

    /**
     * Arrête tous les processus Python en cours.
     */
    public static void stopPythonProcesses() {
        try {
            String command = "taskkill /f /im python*";
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();
            process.waitFor();
            process.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arrête un thread spécifique en fonction de son nom.
     *
     * @param _threadName Le nom du thread à arrêter.
     */
    public static void stopThreadByName(String _threadName) {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if (thread.getName().toLowerCase().equals(_threadName.toLowerCase())) {
                thread.interrupt();
                break;
            }
        }
    }
}