package org.fhnw.aigs.RockPaperScissors.client;

import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Clientseitiges Grundgerüst für ein AIGS-Spiel.<br>
 * Dient zur clientseitigen Behandlung des Spiels
 * @author Raphael Stoeckli
 */
public class RockPaperScissorsClientGame extends ClientGame{
    
    /**
     * Konstruktor mit Argumenten
     * @param gameName Name des Spiels
     * @param mode GameModus (SinglePlayer, MultiPlayer)
     */
    public RockPaperScissorsClientGame(String gameName, GameMode mode)
    {
        super(gameName, mode);                                                  // Rufe Konstruktor von Super-Klasse auf
        
    }

    
    /**
     * Methode verarbeitet die Nachrichten vom Server
     * @param message Nachricht vom Server. Nachrichten müssen als von Message abgeleitete Klassen im Package commons vorhanden sein
     */
    @Override                                                                   // Automatisch generierte (abstrakte) Methode, welche noch implementiert werden muss.
    public void processGameLogic(Message message) {
        throw new UnsupportedOperationException("Not supported yet.");          // Wirft momentan eine Exception. Wird später gegen Code ausgetauscht
    }

    /**
     * Methode wird ausgeführt, sobald das Spiel startklar ist
     */
    @Override                                                                   // Aus Super-Klasse (abstrakt) implementierte Methode
    public void onGameReady() {
        startGame();                                                            // Startet Spiel über Methode der Super-Klasse
        // Hier folgen Aktionen um das Spiel aufzusetzen.
        // Etwaige Parameter werden über eine spezielle Nachricht an den Server geschickt.
        // Ausserdem kann ds Status-Label im Haupt-Fenster angepasst werden (z.B. "Game started")
        // ------------------------------
    }
    
}
