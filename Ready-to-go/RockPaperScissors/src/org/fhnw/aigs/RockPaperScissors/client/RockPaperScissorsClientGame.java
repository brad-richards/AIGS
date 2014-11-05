package org.fhnw.aigs.RockPaperScissors.client;

import javax.swing.JOptionPane;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsParticipantsMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsResultMessage;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.GameStartMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Klasse zur Abbildung der Clientseitigen Logik.<br>
 * Abgeleitet von ClientGame aus dem AIGS BaseClient<br>
 * v1.0 Initial release<br>
 * v1.0.1 Minor chamnges due to dependencies
 * @author Raphael Stoeckli
 * @version v1.0.1
 */
public class RockPaperScissorsClientGame extends ClientGame{
    
    // VARIBELDEFINITIONEN
    private RockPaperScissorsBoard clientBoard;                                 // Definition des Spielfeldes

    /**
     * Gibt das zugehörige Spielfeld zurück
     * @return RockPaperScissorsBoard-Objekt
     */
    public RockPaperScissorsBoard getClientBoard() {
        return clientBoard;
    }

    /**
     * Setzt das zugehörige Spielfeld
     * @param clientBoard RockPaperScissorsBoard-Objekt
     */
    public void setClientBoard(RockPaperScissorsBoard clientBoard) {
        this.clientBoard = clientBoard;
    }
    
    /**
     * Konstruktor mit Argumenten
     * @param gameName Name des Spiels
     * @param mode GameModus (SinglePlayer, MultiPlayer)
     * @param version Versiond es Programms
     */
    public RockPaperScissorsClientGame(String gameName, String version, GameMode mode)
    {
        // WICHTIG - Aufruf der Super-Klasse
        super(gameName, version, mode);                                          // Rufe Konstruktor von Super-Klasse auf: Name, Version (optional), Game Mode
    }

    
    /**
     * Methode verarbeitet die Nachrichten vom Server
     * @param message Nachricht vom Server. Nachrichten müssen als von Message abgeleitete Klassen im Package commons vorhanden sein
     */
    @Override                                                                   // Implementiert Methode
    public void processGameLogic(Message message) {
        if (message instanceof GameStartMessage) {                              // Wenn message vom Typ GameStartMessage ist...
            gameWindow.fadeOutOverlay();                                        // Warte-Bildschirm ausbelnden (Spielfeld wird sichtbar)
        }
        else if (message instanceof RockPaperScissorsParticipantsMessage)       // Wenn message vom Typ RockPaperScissorsParticipantsMessage ist...
        {
            RockPaperScissorsParticipantsMessage msg = (RockPaperScissorsParticipantsMessage)message; // Casten ins richtige Format
            if (msg.getPlayerOne().getName().equals(player.getName()))          // Wenn der Name des übergebenen Spielers eins der eigene Name ist...
            {
                clientBoard.manilupateText(msg.getPlayerTwo().getName(), 0, 0); // Name des Gegners auf dem Spielfeld eintragen
            }
            else                                                                // ...sonst
            {
               clientBoard.manilupateText(msg.getPlayerOne().getName(), 0, 0);  // Name des Gegners auf dem Spielfeld eintragen
            }
            clientBoard.manipulateHeader("Warte auf Zug von Gegenspieler...");  // Text in Header anpassen
        }
        else if (message instanceof RockPaperScissorsResultMessage){            // Wenn message vom Typ RockPaperScissorsResultMessage ist...
             clientBoard.manipulateHeader("Zug wurde beendet");
             RockPaperScissorsResultMessage result = (RockPaperScissorsResultMessage)message;  // Caste ins richtige Format
                                                                                // Manipulieren der Bilder und Texte auf dem Spielfeld
             clientBoard.manipulateGUI(result.getOpponentSymbol(), result.getMyState(), result.getMySymbol(), result.getOpponentName(), result.getOpponentPoints(), result.getMyPoints());
             clientBoard.nextTurn(result.getTurnMessage(), result.isIsLastTurn()); // Vorbereiten des Spielfledes auf nächsten Zug
         }
        else if (message instanceof GameEndsMessage) {                          // Wenn message vom Typ GameEndsMessage ist...
            setNoInteractionAllowed(true);                                      // Sperre alle Eingaben (Verhindert Probleme auf Client und Server etc.)
            GameEndsMessage gameEndMessage = (GameEndsMessage) message;         // Caste ins richtige Format
            JOptionPane.showMessageDialog(null, gameEndMessage.getReason(), "Game ends", JOptionPane.INFORMATION_MESSAGE); // Gebe Gewinner und letzte Meldung des Spiels aus
            System.exit(0);                                                     // Programm beenden
        }
    }

    /**
     * Methode wird ausgeführt, sobald das Spiel startklar ist
     */
    @Override                                                                   // Impementierte Methode
    public void onGameReady() {
        // WICHTIG - Wird dieser Aufruf nicht gemacht, startet das Spiel nicht
        startGame();                                                            // Startet Spiel über Methode der Super-Klasse
        clientBoard.manipulateHeader("Warte auf Gegenspieler...");              // Text in Header anpassen
    }
    
}
