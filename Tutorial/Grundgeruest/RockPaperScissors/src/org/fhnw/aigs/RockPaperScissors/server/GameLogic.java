package org.fhnw.aigs.RockPaperScissors.server;

import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Serverseitiges Grundgerüst für ein AIGS-Spiel<br>
 * @author Raphael Stoeckli
 */
public class GameLogic extends Game {
    
    public static final String GAMENAME = "RockPaperScissors";                  // Name des Spiels, definiert als Konstante. Um Client- von Server-Logik zu trennen wird nicht der Wert aus Main.GAMENAME verwendet
    public static final int MINNUMBEROFPLAYERS = 2;                             // Minimale Anzahl von Spielern für eine Partie als Konstante
    
    /**
     * Parameterloser Konstruktor (wird vorausgesetzt, WICHTIG!)
     */
    public GameLogic()
    {
        super(GameLogic.GAMENAME, GameLogic.MINNUMBEROFPLAYERS);                // Rufe Konstruktor von Super-Klasse auf. 
    }

    /**
     * Methode zum Einleiten/Vorbereiten des Spiels
     */
    @Override                                                                   // Automatisch generierte (abstrakte) Methode, welche noch implementiert werden muss.
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");          // Wirft momentan eine Exception. Wird später gegen Code ausgetauscht
    }

    /**
     * Methode verarbeitet die Nachrichten von Clients
     * @param msg Nachricht vom Client. Nachrichten müssen als abgeleitete Klassen im Package commons vorhanden sein
     * @param player Spieler, welcher die Nachricht geschickt hat
     */
    @Override                                                                   // Automatisch generierte (abstrakte) Methode, welche noch implementiert werden muss.
    public void processGameLogic(Message msg, Player player) {
        throw new UnsupportedOperationException("Not supported yet.");          // Wirft momentan eine Exception. Wird später gegen Code ausgetauscht
    }

    /**
     * Methode zum Überprüfen, ob die Partie mit einem Zug beendet wird.<br>
     * Die Methode wird bei jedem Zug geprüft.
     */
    @Override                                                                   // Automatisch generierte (abstrakte) Methode, welche noch implementiert werden muss.
    public void checkForWinningCondition() {
        throw new UnsupportedOperationException("Not supported yet.");          // Wirft momentan eine Exception. Wird später gegen Code ausgetauscht
    }
    
}
