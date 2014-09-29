package org.fhnw.aigs.RockPaperScissors.server;

import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;
import org.fhnw.aigs.commons.Player;

/**
 * Klasse zur Darstellung des aktuellen Zuges (Turn) eines Spielers. Objekte dieser Klasse werden auf dem Server für die Verwaltung der Züge benötigt
 * @author Raphael Stoeckli
 */
public class RockPaperScissorsTurn {
    
    // VARIBELDEFINITIONEN   
    private Player player;                                                      // Spieler (Player) des Zuges 
    private boolean hasTurnFinished;                                            // Gibt an, ob der aktuelle Zug beendet wurde (true), oder noch nicht (false)
    private RockPaperScissorsSymbol turnSymbol;                                 // Gewähltes Symbol des aktuellen Zuges (Nichts, Schere, Stein, Papier)
    private int points;                                                         // Punktzahl des Spielers (über alle Züge)
    private GameState turnState;                                                // Status des aktuellen Zuges (noch nicht gewählt, gewonnen, verloren, unentschieden)
    private int opponentIndex;                                                  // Index (für ArrayListe aller Turn-Objekte) des Gegners --> Annahme: Es gibt nur einen Gegner

    /**
     * Gibt den gegnerischen Index zurück
     * @return Index für ArrayListe
     */
    public int getOpponentIndex() {
        return opponentIndex;
    }

    /**
     * Setzt gegnerischen Index 
     * @param opponentIndex Index für ArrayListe
     */
    public void setOpponentIndex(int opponentIndex) {
        this.opponentIndex = opponentIndex;
    }

    /**
     * Gibt Status des aktuellen Zuges zurück
     * @return Status des Zuges
     */
    public GameState getTurnState() {
        return turnState;
    }

    /**
     * Setzt Status des aktuellen Zuges
     * @param turnState Status des Zuges
     */
    public void setTurnState(GameState turnState) {
        this.turnState = turnState;
    }
    
    /**
     * Überladung: Setzt Status des aktuellen Zuges und zählt Punktzahl hinzu
     * @param turnState Status des Zuges
     * @param points Anzahl Punkte, die hinzugezählt werden sollen (i.d. Regel 0 oder 1)
     */
    public void setTurnState(GameState turnState, int points) {
        this.turnState = turnState;
        this.points += points;
    }

    /**
     * Gibt Spieler des Zuges zurück                                            // Nur get-Methode, da Payer nicht mehr geändert werden soll. Wird in Konstruktor einmalig übergeben
     * @return Player-Objekt
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gibt zurück, ob Zug beendet wurde
     * @return true, wenn Zug beendet wurde, ansonsten false
     */
    public boolean hasTurnFinished() {
        return hasTurnFinished;
    }

    /**
     * Setzt, ob Zug beendet wurde
     * @param hasTurnFinished true, wenn Zug beendet wurde, ansonsten false
     */
    public void setTurnFinished(boolean hasTurnFinished) {
        this.hasTurnFinished = hasTurnFinished;
    }

    /**
     * Gibt das Symbol des aktuellen Zuges zurück
     * @return Symbol des Zuges
     */
    public RockPaperScissorsSymbol getTurnSymbol() {
        return turnSymbol;
    }

    /**
     * Setzt das Symbol des aktuellen Zuges
     * @param turnSymbol Symbol des Zuges
     */
    public void setTurnSymbol(RockPaperScissorsSymbol turnSymbol) {
        this.turnSymbol = turnSymbol;
    }

    /**
     * Gibt Punktzahl des Spielers zurück
     * @return Punktzahl
     */
    public int getPoints() {
        return points;
    }

    /**
     * Setzt Punktzahl des Spielers
     * @param points Punktzahl
     */
    public void setPoints(int points) {
        this.points = points;
    }
    
    /**
     * Konstruktor mit Übergabe des Spielers
     * @param player Spieler dieses Objekts 
     */
    public RockPaperScissorsTurn(Player player)
    {
        this.player = player;                                                   // Setze alle Parameter (folgende Zeilen)
        this.hasTurnFinished = false;
        this.points = 0;
        this.turnSymbol = RockPaperScissorsSymbol.None;
        this.turnState = GameState.None;
    }
    
    /**
     * Methode bereitet alles für nächsten Zug vor
     */
    public void nextTurn()
    {
        this.hasTurnFinished = false;                                           // Setzte beendet auf false
        this.turnSymbol = RockPaperScissorsSymbol.None;                         // Setze Symbol auf nichts ausgewählt
        this.turnState = GameState.None;                                        // Setze Status auf nicht definiert
    }
    
    /**
     * Gibt den Namen des Spielers zurück (Abkürzung)
     * @return Spielername aus Player-Objekt
     */
    public String getPlayerName()
    {
        return this.player.getName();
    }
    
    /**
     * Gibt die ID des Spielers zurück (Abkürzung)
     * @return ID aus Player-Objekt
     */
    public int getPlayerID()
    {
        return this.player.getId();
    }
}
