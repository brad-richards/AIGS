package org.fhnw.aigs.RockPaperScissors.client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSelectionMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;

/**
 * Klasse für Definition des Spielfelds.<br>
 * Die Klasse ist von GridPane abgeleitet und wird im AIGS BaseClient eingebaut
 * @author Raphael Stoeckli
 */
public class RockPaperScissorsBoard extends GridPane {                          // Der zentrale Teil des Spiels basiert immer auf einem GridPane (bei JavaFX)
    
    // VARIBELDEFINITIONEN
    private RockPaperScissorsClientGame clientGame;                             // ClientGame-Objekt
    private GridPane fieldsPane;                                                // GridPane für eigene Auswahlfelder
    private GridPane opponentPane;                                              // GridPane für gegnerische Anzeige
    private Label opponentLabel;                                                // Label für Namen und Punktzahl des Gegners
    private Label myLabel;                                                      // Label für eigenen Namen und Punktzahl
    private Separator separator;                                                // Horizontale Linie (zur Gestaltung)
    private RockPaperScissorsBoardPane opponentField;                           // Anzeigefeld des Gegners
    private RockPaperScissorsBoardPane rockField;                               // Eigenes Auswahlfeld (Stein)
    private RockPaperScissorsBoardPane paperField;                              // Eigenes Auswahlfeld (Papier)
    private RockPaperScissorsBoardPane scissorsField;                           // Eigenes Auswahlfeld (Schere)

    
    /**
     * Konstruktor mit Argument
     * @param clientGame Das ClientGame-Objekt des Spiels
     */
    public RockPaperScissorsBoard(RockPaperScissorsClientGame clientGame)
    {
        this.clientGame = clientGame;                                           // Setze ClientGame-Objekt
        
        opponentField = new RockPaperScissorsBoardPane();                       // Definiere Anzeige des Gegners
        rockField = new RockPaperScissorsBoardPane();                           // Definiere eigenes Feld für Stein
        paperField = new RockPaperScissorsBoardPane();                          // Definiere eigenes Feld für Papier
        scissorsField = new RockPaperScissorsBoardPane();                       // Definiere eigenes Feld für Schere
        opponentLabel = new Label("Gegner: 0");                                 // Definiere Label für Namen un Punkte des Gegners (wird später überschrieben)
        myLabel = new Label("Ich: 0");                                          // Definiere einenes Label für Namen un Punkte (wird später überschrieben)
        separator = new Separator(Orientation.HORIZONTAL);                      // Definiere horizontale Line als Gestaltungselement
        fieldsPane = new GridPane();                                            // Definiere GridPane für die drei eigenen Felder (Schere, Stein, Papier)
        opponentPane = new GridPane();                                          // Definiere Gridpane für Anzeige des Gegeners (Muss so gelöst werden, da Element sonst nicht zentriert werden kann)
        
        opponentField.setSymbol(RockPaperScissorsSymbol.None, GameState.None);  // Leerer Hintergrund
        rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.None);      // Zeige Stein an        
        paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.None);    // Zeige Papier an
        scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.None); //Zeige Schere an
        opponentLabel.getStyleClass().add("playerLabel");                       // Setze Style des Labels (CSS wurde bereits in Main-Klasse eingebunden)
        myLabel.getStyleClass().add("playerLabel");                             // Setze Style des Labels (CSS wurde bereits in Main-Klasse eingebunden)
        separator.getStyleClass().add("separator");                             // Setze Style der horizontalen Linie (CSS wurde bereits in Main-Klasse eingebunden)
        
        opponentPane.add(opponentField, 0, 0);                                  // Füge Feld des Gegners in GridPane (opponentPane) an Position x0/y0 ein
        opponentPane.setAlignment(Pos.CENTER);                                  // Zentriere alle Elemente in opponentPane        
        fieldsPane.add(rockField, 0, 0);                                        // Füge Feld (Stein) zu GridPane (fieldsPane) an Position x0/y0 hinzu
        fieldsPane.add(paperField, 1, 0);                                       // Füge Feld (Papier) zu GridPane (fieldsPane) an Position x1/y0 hinzu
        fieldsPane.add(scissorsField, 2, 0);                                    // Füge Feld (Schere) zu GridPane (fieldsPane) an Position x2/y0 hinzu
        fieldsPane.vgapProperty().set(10);                                      // Definiere vertikalen Rand von 10 Pixel zwischen Elementen von fieldsPane
        fieldsPane.hgapProperty().set(10);                                      // Definiere horizontalen Rand von 10 Pixel zwischen Elementen von fieldsPane
        fieldsPane.setPadding(new Insets(10,10,10,10));                         // Definiere Innenrand von 10 Pixel zum (unsichtbaren) Ramen von fieldsPane
       
    //    this.fieldsPane.gridLinesVisibleProperty().set(true);                 // Falls eingeblendet: Rahmen von fieldsPane wird angezeigt (DEBUG)
    //    this.gridLinesVisibleProperty().set(true);                            // Falls eingeblendet: Rahmen von diesem Fenster wird angezeigt (DEBUG)
         
        this.add(opponentLabel, 0, 0);                                          // Füge Label (opponentLabel) im Fenster an Position x0/y0 hinzu
        this.add(opponentPane, 0, 1);                                           // Füge Pane (opponentPane) im Fenster an Position x0/y1 hinzu
        this.add(separator, 0, 2);                                              // Füge Spearator (separator) im Fenster an Position x0/y2 hinzu
        this.add(fieldsPane, 0, 3);                                             // Füge Pane (fieldsPane) im Fenster an Position x0/y3 hinzu
        this.add(myLabel, 0, 4);                                                // Füge Label (myLabel) im Fenster an Position x0/y4 hinzu
        this.vgapProperty().set(20);                                            // Definiere vertikalen Rand von 20 Pixel zwischen Elementen vom Fenster
        this.setAlignment(Pos.CENTER);                                          // Zentriere alle hinzugefügten Elemente des Fensters
                
        // EVENTHANDLER                                                         // Drei EventHandler für Auswahlfelder (Schere, Stein, Papier) definieren
        
        rockField.setOnMouseClicked(new EventHandler<MouseEvent>() {            // Definition von Eventhandler für Klick auf Stein-Auswahlfeld
            @Override                                                           // Impementierte Methode
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Rock);                  // Markiere Feld als ausgewählt (rufe setSelectedPane auf)
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Rock); // Erstelle Nachricht um Server die Auswahl mitzuteilen
                clientGame.sendMessageToServer(msg);                            // Sende Nachricht an Server (Verbindung wird über ClientGame hergestellt)
            }
        });                
        
        paperField.setOnMouseClicked(new EventHandler<MouseEvent>() {           // Definition von Eventhandler für Klick auf Papier-Auswahlfeld
            @Override                                                           // Impementierte Methode
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Paper);                 // Markiere Feld als ausgewählt (rufe setSelectedPane auf)
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Paper); // Erstelle Nachricht um Server die Auswahl mitzuteilen
                clientGame.sendMessageToServer(msg);                            // Sende Nachricht an Server (Verbindung wird über ClientGame hergestellt)
            }
        });
        
        scissorsField.setOnMouseClicked(new EventHandler<MouseEvent>() {        // Definition von Eventhandler für Klick auf Schere-Auswahlfeld
            @Override                                                           // Impementierte Methode
            public void handle(MouseEvent event) {
                setSelectedPane(RockPaperScissorsSymbol.Scissors);              // Markiere Feld als ausgewählt (rufe setSelectedPane auf)
                RockPaperScissorsSelectionMessage msg = new RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol.Scissors); // Erstelle Nachricht um Server die Auswahl mitzuteilen
                clientGame.sendMessageToServer(msg);                            // Sende Nachricht an Server (Verbindung wird über ClientGame hergestellt)    
            }
        });
  
    }
    
    /**
     * Methode setzt zuerst alle Felder zurück (Styles) und wählt dann das angegebene Feld aus.<br>
     * Es werden dabei nur Klassen aus dem CSS ausgetauscht
     * @param symbol Symbol, welches ausgewählt wurde (Nichts, Schere, Stein, Papier)
     */
    private void setSelectedPane(RockPaperScissorsSymbol symbol)
    {
        Platform.runLater(new Runnable() {                                  // WICHTIG! Styles dürfen nicht direkt verändert werden, sondern müssen in Platform.runLater abgearbeitet werden
        @Override                                                           // Impementierte Methode
        public void run() {
            rockField.getStyleClass().remove("rockPaperScissorsField");             // Entferne alle Styles vom Stein-Feld
            rockField.getStyleClass().remove("rockPaperScissorsFieldSelected");     // "
            paperField.getStyleClass().remove("rockPaperScissorsField");            // Entferne alle Styles vom Papier-Feld
            paperField.getStyleClass().remove("rockPaperScissorsFieldSelected");    // "
            scissorsField.getStyleClass().remove("rockPaperScissorsField");         // Entferne alle Styles vom Schere-Feld
            scissorsField.getStyleClass().remove("rockPaperScissorsFieldSelected"); // "     
            if (symbol == RockPaperScissorsSymbol.Paper)                            // Setze Style, falls Papier ausgewählt
            {
                paperField.getStyleClass().add("rockPaperScissorsFieldSelected");
            }
            else if (symbol == RockPaperScissorsSymbol.Rock)                        // Setze Style, falls Stein ausgewählt
            {
                rockField.getStyleClass().add("rockPaperScissorsFieldSelected");
            }
            else if (symbol == RockPaperScissorsSymbol.Scissors)                    // Setze Style, falls Schere ausgewählt
            {
                scissorsField.getStyleClass().add("rockPaperScissorsFieldSelected");
            }
        }});
        // Wurde nichts ausgewählt werden nur alle Styles entfernt (z.B. bei einem neuen Zug)
    }
    
    /**
     * Methode manipuliert die Bilder (vom Gegner und eigen) und der Text dazu
     * @param opponentSymbol Symbol, welches vom Gegener gewählt wurde
     * @param myState Mein Status (Gewonnen, Verloren, Unentschieden)
     * @param mySymbol Mein ausgewähltes Symbol (Schere, Stein, Papier)
     * @param opponentName Name des Gegners
     * @param opponentPoints Punktzahl (total) des Gegners
     * @param myPoints Meine Punktzahl
     */
    public void manipulateGUI(RockPaperScissorsSymbol opponentSymbol, GameState myState, RockPaperScissorsSymbol mySymbol, String opponentName, int opponentPoints, int myPoints){ 
        manipulateGUI(opponentSymbol, myState, mySymbol);                       // Ruft Methode zum manipulieren der Felder auf
        manilupateText(opponentName, opponentPoints, myPoints);                 // Ruft Methode zum Manipulieren der Texte auf
    }
    
    /**
     * Methode manipuliert die Bilder (vom Gegner und eigen)
     * @param opponentSymbol Symbol, welches vom Gegener gewählt wurde
     * @param myState Mein Status (Gewonnen, Verloren, Unentschieden)
     * @param mySymbol Mein ausgewähltes Symbol (Schere, Stein, Papier)
     */
    public void manipulateGUI(RockPaperScissorsSymbol opponentSymbol, GameState myState, RockPaperScissorsSymbol mySymbol){ 
        if (myState == GameState.Win)                                           // Falls Ich gewonnen habe...
        {
            opponentField.setSymbol(opponentSymbol, GameState.Lose);            // Setze Symbol des Gegners (er hat verloren)
            if (mySymbol == RockPaperScissorsSymbol.Rock)                       
            { rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Win); } // Setze Haken, falls ich Stein gewählt habe
            else if (mySymbol == RockPaperScissorsSymbol.Paper)
            { paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Win); } // Setze Haken, falls ich Papier gewählt habe
            else
            { scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Win); } // Setze Haken, falls ich Schere gewählt habe
        }
        else if (myState == GameState.Lose)                                     // Falls ich verloren habe...
        {
            opponentField.setSymbol(opponentSymbol, GameState.Win);             // Setze Symbol des Gegners (er hat gewonnen)
            if (mySymbol == RockPaperScissorsSymbol.Rock)
            { rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Lose); } // Setze Kreuz, falls ich Stein gewählt habe
            else if (mySymbol == RockPaperScissorsSymbol.Paper)
            { paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Lose); } // Setze Kreuz, falls ich Papier gewählt habe
            else
            { scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Lose); } // Setze Kreuz, falls ich Schere gewählt habe       
        }
        else                                                                    // Fals unentschieden ist...
        {
            opponentField.setSymbol(opponentSymbol, GameState.Draw);            // Setze Symbol des Gegners (unentschieden = nur Symbol anzeigen, kein Overlay)
            rockField.setSymbol(RockPaperScissorsSymbol.Rock, GameState.Draw);         // Setze kein Kreuz oder Haken bei Stein
            paperField.setSymbol(RockPaperScissorsSymbol.Paper, GameState.Draw);       // Setze kein Kreuz oder Haken bei Papier
            scissorsField.setSymbol(RockPaperScissorsSymbol.Scissors, GameState.Draw); // Setze kein Kreuz oder Haken bei Schere
        }    
    }
    
    /**
     * Methode manipuliert Texte (Namen und Punktzahlen)<br>
     * Die Manipulation muss in Platform.runLater erfolgen um einen Absturz zur Laufzeit zu verhindern
     * @param opponentName Name des Gegners
     * @param opponentPoints Punktzahl des Gegners
     * @param myPoints Meine Punktzahl
     */
    public void manilupateText(String opponentName, int opponentPoints, int myPoints)
    {
            Platform.runLater(new Runnable() {                                  // WICHTIG! Texte dürfen nicht direkt auf den Labels geändert werden, sondern müssen in Platform.runLater abgearbeitet werden
            @Override                                                           // Impementierte Methode
            public void run() {
                opponentLabel.setText(opponentName + ": " + Integer.toString(opponentPoints) + " Punkte"); // Setze Text des Gegners
                myLabel.setText("Ich: " + Integer.toString(myPoints) + " Punkte"); // Setze meinen Text
            }
        });       
    }
    
    /**
     * Methode ändert Text im Header des Fensters (rechts neben Spielname)
     * @param text Text, welcher eingesetzt werden soll
     */
    public void manipulateHeader(String text)
    {
        clientGame.getGameWindow().getHeader().setStatusLabelText(text);        // Über ClientGame-Objekt wird das Hauptfenster und den Header darin angesprochen
    }
    
    /**
     * Methode bereitet den Client auf den nächsten Zug vor
     * @param message Nachricht, wer mit welchem Symbol gewonnen hat
     * @param lastTurn Falls es sich um den aller letzten Zug der Partie handelt true, ansonsten false
     */
   public void nextTurn(String message, boolean lastTurn)
   {
       if (lastTurn == false)                                                   // Nicht Letzter Zug
       {
        JOptionPane.showMessageDialog(null, message + "\nAuf OK klicken für den nächsten Zug.", "Zug beendet", JOptionPane.INFORMATION_MESSAGE); // Zeige Dialogbox an
       }
       else                                                                     // Letzter Zug
       {
        JOptionPane.showMessageDialog(null, message + "\nAuf OK klicken um den Gewinner aller Züge anzuzeigen.", "Zug beendet", JOptionPane.INFORMATION_MESSAGE); // Zeige Dialogbox an
       }
       manipulateGUI(RockPaperScissorsSymbol.None, GameState.None, RockPaperScissorsSymbol.None); // Auswahl des Gegners, sowie Kreuz und Haken entfernen für neuen Zug
       setSelectedPane(RockPaperScissorsSymbol.None);                           // Die eigenen Auswahl für den nächsten Zug entfernen
       manipulateHeader("Warte auf Zug von Gegenspieler...");                   // Setze Status-Text im Header
   }
 
}
