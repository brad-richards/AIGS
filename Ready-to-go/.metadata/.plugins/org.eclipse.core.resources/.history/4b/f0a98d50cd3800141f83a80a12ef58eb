package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.client.GUI.LoadingWindow;
import org.fhnw.aigs.client.communication.ClientCommunication;
import org.fhnw.aigs.client.communication.Settings;
import org.fhnw.aigs.commons.GameMode;

/**
 * Clientseitiges Grundgerüst für ein AIGS-Spiel<br>
 * Klasse startet Programm über javaFX (Abgeleitet von Application). 
 * @author Raphael Stoeckli
 */
public class Main extends Application{
    
    // KONSTANTEN
    public static final String GAMENAME = "RockPaperScissors";                  // Name des Spiels, definiert als Konstante
    public static final GameMode GAMEMODE = GameMode.Multiplayer;               // GameMode als Konstante

    /**
     * Start-Methode von JavaFX. Überspielt main()-Methode automatisch
     * @param primaryStage Top-Level der JavaFX-Darstellung
     * @throws Exception 
     */
    @Override                                                                   // Impementierte Methode
    public void start(Stage primaryStage) throws Exception {
                                                                                
        BaseGameWindow root = new BaseGameWindow(primaryStage, Main.GAMENAME);  // Erstelle eine Instanz des Fensters vom BaseClient (BaseGameWindow)
        root.getStylesheets().add("/Assets/Stylesheets/RockPaperScissors.css"); // Registriere CSS-Datei in Package für Spiel (Assets)
        primaryStage.setTitle(Main.GAMENAME);                                   // Setze Titel auf dem Fenster (Kann auch anders heissen)
        primaryStage.show();                                                    // Zeige Fenster an
        
                                                                                // Erstelle eine Instanz der abgeleiteten Klasse RockPaperScissorsClientGame. Setze den Spielnamen und den GameMode
        RockPaperScissorsClientGame clientGame = new RockPaperScissorsClientGame(Main.GAMENAME, Main.GAMEMODE);      

        RockPaperScissorsBoard board = new RockPaperScissorsBoard(clientGame);  // Erstelle neues Spielfeld (abgeleitet von GridPane)      
        root.setContent(board);                                                 // Binde Spielfeld in Hauptfenster ein
        
        clientGame.setGameWindow(root);                                         // Bilde Referenz zum Hauptenster, damit Angaben, wie Status oder Titel während dem Spiel geändert werden können
        clientGame.setClientBoard(board);                                       // Bilde Referenz zum Spielfeld, für einfacheren Zugriff auf dessen Methoden
        
        root.setOverlay(new LoadingWindow());                                   // Ladebildschirm wird über Fester gelegt
        Settings.tryLoadSettings(true);                                         // Versuche Settings zu laden und zeige Settings-Fenster, falls nicht vorhanden
        
                                                                                // Versuche den Server zu erreichen. Angaben stammen aus den Setttings, plus das clientGame-Objekt zur Identifizierung
        ClientCommunication.setCredentials(clientGame, Settings.getInstance().getServerAddress(), Settings.getInstance().getServerPort());
        
        Thread communicationThread = new Thread(                                // Erzeuge einen Thread mit der Kommunikation, um das Fenster nicht "einzufrieren"
        ClientCommunication.getInstance());                                     // Als Objekt für den Thread dient eine automatisch gebildetete Instanz von ClientCommunication
        communicationThread.start();                                            // Startet den Thread, welcher endlos bis zum Programmende läuft.
        
    }
    
     /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
    
    
}
