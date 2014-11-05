package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Klasse zur Mitteilung des Servers an einen Client, wie das Resultat eines Zuges ist.<br>
 * Das resultat ist immer aus der Sicht des jeweiligen Clients. Die selbe Nachricht darf also nicht an alle Clients gesendet, sondern muss pro Client definiert werden.
 * Aus Message abgeleitete Klasse.
 * @author Raphael Stoeckli
 * @version v1.0
 */
@XmlRootElement(name = "RockPaperScissorsResultMessage")                        // Stellt sicher, dass Daten aus dieser Klasse korrekt in XML umgesetzt und versendet werden können (Kommunikation)
public class RockPaperScissorsResultMessage extends Message {
    
    // VARIBELDEFINITIONEN    
    private RockPaperScissorsSymbol mySymbol;                                   // Selbst gewähltes Symbol
    private RockPaperScissorsSymbol opponentSymbol;                             // Symbol des Gegners
    private int turn;                                                           // Nummer des Zuges (i.d. Regel zwischen 1 bis 3)
    private int myPoints;                                                       // Eigene Punktzahl (wird nicht auf Client gespeichert)
    private int opponentPoints;                                                 // Punktzahl des Gegners
    private String opponentName;                                                // Name des Gegners (Wird benötigt um den String 'Name: Punkzahl' unkomliziert zusammenzubauen)
    private boolean isLastTurn;                                                 // Angabe, ob es sich um den letzten Zug der Partie gehandelt hat
    private String turnMessage;                                                 // Nachricht zum Verlauf des Zuges (z.B. "Schere schlägt Papier. Player1 gewinnt!")
    private GameState myState;                                                  // Eiegner Status (gewonnen, Verloren oder unentschieden). Der Status des Gegeners kann daraus abgeleitet werden
    
    /**
     * Gibt eiegnes Symbol zurück
     * @return Symbol-Objekt
     */
    @XmlElement(name = "MySymbol")                                              // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public RockPaperScissorsSymbol getMySymbol() {
        return mySymbol;
    }

    /**
     * Setzt eigenes Symbol
     * @param mySymbol Symbol-Objekt
     */
    public void setMySymbol(RockPaperScissorsSymbol mySymbol) {
        this.mySymbol = mySymbol;
    }

    /**
     * Gibt Symbol des Gegners zurück
     * @return Symbol-Objekt
     */
    @XmlElement(name = "OpponentSymbol")                                        // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)    
    public RockPaperScissorsSymbol getOpponentSymbol() {
        return opponentSymbol;
    }

    /**
     * Setzt Symbol des Gegners
     * @param opponentSymbol Symbol-Objekt
     */
    public void setOpponentSymbol(RockPaperScissorsSymbol opponentSymbol) {
        this.opponentSymbol = opponentSymbol;
    }

    /**
     * Gibt eigene Punktzahl zurück
     * @return Punktzahl
     */
    @XmlElement(name = "MyPoints")                                              // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public int getMyPoints() {
        return myPoints;
    }

    /**
     * Setzt eigene Punktzahl
     * @param myPoints Punktzahl
     */
    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    /**
     * Gibt Punktzahl des Gegners zurück
     * @return Punktzahl
     */
    @XmlElement(name = "OpponentPoints")                                        // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public int getOpponentPoints() {
        return opponentPoints;
    }

    /**
     * Setzt Punktzahl des Gegners
     * @param opponentPoints Punktzahl
     */
    public void setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    /**
     * Gibt Name des Gegners zurück
     * @return Name von Gegner
     */
    @XmlElement(name = "OpponentName")                                          // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * Setzt Name des Gegners
     * @param opponentName Name von Gegner
     */
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    /**
     * Gibt eigenen Status (Gewonnen, Verloren, Unentschieden) zurück
     * @return GameState-Objekt
     */
    @XmlElement(name = "MyState")                                               // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public GameState getMyState() {
        return myState;
    }

    /**
     * Setzt eigenen Status (Gewonnen, Verloren, Unentschieden)
     * @param state GameState-Objekt
     */
    public void setMyState (GameState state) {
        this.myState = state;
    }

    /**
     * Gibt Nachricht zum Zug zurück
     * @return Nachricht
     */
    @XmlElement(name = "TurnMessage")                                           // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public String getTurnMessage() {
        return turnMessage;
    }

    /**
     * Setzt Nachricht zum Zug
     * @param turnMessage Nachricht
     */
    public void setTurnMessage(String turnMessage) {
        this.turnMessage = turnMessage;
    }
    
    /**
     * Gibt zurück, ob es sich um den letzten Zug der Partie handelt
     * @return true, falls es der letzte Zug ist, ansonsten false
     */
    @XmlElement(name = "IsLastTurn")                                            // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public boolean isIsLastTurn() {
        return isLastTurn;
    }

    /**
     * Setzt, ob es sich um den letzten Zug der Partie handelt
     * @param isLastTurn true, falls es der letzte Zug ist, ansonsten false
     */
    public void setIsLastTurn(boolean isLastTurn) {
        this.isLastTurn = isLastTurn;
    }

    /**
     * Gibt Nummer des Zuges zurück
     * @return Nummer
     */
    @XmlElement(name = "Turn")                                                  // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public int getTurn() {
        return turn;
    }

    /**
     * Setzt Nummer des Zuges
     * @param turn Nummer
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }
    
    /**
     * Parameterloser konstruktor (wird zwingend benötigt)
     */
    RockPaperScissorsResultMessage(){                                           // Keine weiteren Aktionen notwendig
    }
    

    /**
     * Konstruktor mit Übergabe aller Parameter
     * @param myState  Eigener Status. Status des Gegners ergibt sich automatisch
     * @param lastTurn frue, wenn es sich um den letzten Zug der Pertie handelt, ansonsten false 
     * @param mySymbol Eigenes Symbol
     * @param opponentSymbol Symbol des Gegners
     * @param opponentName Name des Gegners
     * @param myPoints Eigene Punktzahl (Total)
     * @param opponentPoints Punktzahl des Gegners (Total)
     * @param turn Nummer des Zuges
     * @param turnMessage Text, welcher auf den Clients angezeigt werden soll
     */
    public RockPaperScissorsResultMessage(GameState myState, boolean lastTurn, RockPaperScissorsSymbol mySymbol, RockPaperScissorsSymbol opponentSymbol, String opponentName, int myPoints, int opponentPoints, int turn, String turnMessage)
    {
      super();                                                                  // Initialisieren der Super-Klasse
      this.setMyState(myState);                                                 // Setzten der Parameter (alle folgenden Zeilen)
      this.setIsLastTurn(lastTurn);
      this.setMyPoints(myPoints);
      this.setMySymbol(mySymbol);
      this.setOpponentName(opponentName);
      this.setOpponentSymbol(opponentSymbol);
      this.setOpponentPoints(opponentPoints);
      this.setTurn(turn);
      this.setTurnMessage(turnMessage);
    }
    
}
