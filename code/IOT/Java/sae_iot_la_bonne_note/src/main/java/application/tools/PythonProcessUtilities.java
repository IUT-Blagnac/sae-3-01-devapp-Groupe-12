package application.tools;

import java.io.IOException;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class PythonProcessUtilities {

    private static Thread pythonThread;
    private static Process pythonProcess;

    public static void startPythonThread(Stage _primaryStage) {
        // pythonThread = new Thread(() -> {
        // String scriptPath = "code\\IOT\\Python\\connect.py";
        // ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
        // try {
        // pythonProcess = processBuilder.start();
        // } catch (IOException e) {
        // AlertUtilities.showAlert(_primaryStage, "Erreur",
        // "Lancement du script Python impossible.",
        // "Une erreur est survenue lors du lancement du script Python."
        // + ".\nCode d'erreur : " + e,
        // AlertType.ERROR);
        // }
        // });
        // pythonThread.setDaemon(false);
        // pythonThread.start();
    }

    public static void stopPythonThread() {
        if (pythonThread != null && pythonProcess != null) {
            if (pythonThread.isAlive() || pythonProcess.isAlive()) {
                // pythonProcess.destroy();
                // pythonThread.interrupt();
            }
        }
    }

    public static boolean isPythonRunning() {
        if (pythonProcess != null) {
            return pythonProcess.isAlive();
        }
        return false;
    }

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
}