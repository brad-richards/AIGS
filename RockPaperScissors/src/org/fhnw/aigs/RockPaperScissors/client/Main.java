package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fhnw.aigs.client.GUI.BaseGameWindow;
import org.fhnw.aigs.commons.GameMode;

/**
 * Clientseitiges Grundgerüst für ein AIGS-Spiel<br>
 * Klasse startet Programm über javaFX (Abgeleitet von Application).<br>
 * v1.0 Initial release<br>
 * v1.1 Changes due to update of AIGSBaseClient and AIGSCommons
 * @author Raphael Stoeckli
 * @version v1.1
 */
public class Main extends Application{
    
    // KONSTANTEN
    public static final String GAMENAME = "RockPaperScissors";                  // Name des Spiels, definiert als Konstante
    public static final GameMode GAMEMODE = GameMode.Multiplayer;               // GameMode als Konstante
    public static final String VERSION = "v1.1";

    /**
     * Start-Methode von JavaFX. Überspielt main()-Methode automatisch
     * @param primaryStage Top-Level der JavaFX-Darstellung
     * @throws Exception 
     */
    @Override                                                                   // Impementierte Methode
    public void start(Stage primaryStage) throws Exception {
                                                                                
        BaseGameWindow root = new BaseGameWindow(primaryStage, Main.GAMENAME);  // Erstelle eine Instanz des Fensters vom BaseClient (BaseGameWindow)
        root.getStylesheets().add("/Assets/Stylesheets/RockPaperScissors.css"); // Registriere CSS-Datei in Package für Spiel (Assets)
        
                                                                                // Erstelle eine Instanz der abgeleiteten Klasse RockPaperScissorsClientGame. Setze den Spielnamen und den GameMode
        RockPaperScissorsClientGame clientGame = new RockPaperScissorsClientGame(Main.GAMENAME, Main.VERSION, Main.GAMEMODE);      
        RockPaperScissorsBoard board = new RockPaperScissorsBoard(clientGame);  // Erstelle neues Spielfeld (abgeleitet von GridPane)      
        clientGame.setGameWindow(root);                                         // Bilde Referenz zum Hauptenster, damit Angaben, wie Status oder Titel während dem Spiel geändert werden können
        clientGame.setClientBoard(board);                                       // Bilde Referenz zum Spielfeld, für einfacheren Zugriff auf dessen Methoden
        root.InitGame(board, clientGame);                                       // Initialisiere das Spiel mit Übergabe von borad und clientGame
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
