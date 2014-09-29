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
    
    public static final String GAMENAME = "RockPaperScissors";                  // Name des Spiels, definiert als Konstante
    public static final GameMode GAMEMODE = GameMode.SinglePlayer;              // GameMode als Konstante

    /**
     * Start-Methode von JavaFX. Überspielt main()-Methode automatisch
     * @param primaryStage Top-Level der JavaFX-Darstellung
     * @throws Exception 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
                                                                                
        BaseGameWindow root = new BaseGameWindow(primaryStage, Main.GAMENAME);  // Erstelle eine Instanz des Fensters vom BaseClient (BaseGameWindow)
        primaryStage.setTitle(Main.GAMENAME);                                   // Setze Titel auf dem Fenster (Kann auch anders heissen)
        primaryStage.show();                                                    // Zeige Fenster an
        
                                                                                // Erstelle eine Instanz der abgeleiteten Klasse RockPaperScissorsClientGame. Setze den Spielnamen und den GameMode
        RockPaperScissorsClientGame clientGame = new RockPaperScissorsClientGame(Main.GAMENAME, Main.GAMEMODE);      

        // --------------------          
        // Hier wird später eine Klasse, abgeleitet von BaseBoard instanziert und per
        // root.setContent(ABGELEITETESboard); eingebunden (ABGELEITETESboard entspricht dem Klassennnamen)
        // ABGELEITETESboard wird in RockPaperScissorsClientGame eingebunden.
        // Alternativ kann eine eigene Klasse definiert werden, welche von GridPane oder Node generell (JavaFX) ableitet.
        // In RockPaperScissorsClientGame wird später eine Methode definiert, welches ABGELEITETESboard hinzufügt
        // --------------------  
        
        clientGame.setGameWindow(root);                                         // Bilde Referenz zum Spiel-Fenster, damit Angaben, wie Status oder Titel während dem Spiel geändert werden können
        
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
