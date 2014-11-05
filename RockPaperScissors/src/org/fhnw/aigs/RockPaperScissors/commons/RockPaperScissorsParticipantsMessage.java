package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Klasse zur Mitteilung des Servers an alle Clients, wer die Mitspieler der Paertie sind.<br>
 * Dies wird zur Darstellung des Gegnernamens benötigt und am Anfang des Spiels enmalig übertragen.<br>
 * Aus Message abgeleitete Klasse.
 * @author Raphael Stoeckli
 * @version v1.0
 */
@XmlRootElement(name = "RockPaperScissorsParticipantsMessage")                  // Stellt sicher, dass Daten aus dieser Klasse korrekt in XML umgesetzt und versendet werden können (Kommunikation)
public class RockPaperScissorsParticipantsMessage extends Message
{
    // VARIBELDEFINITIONEN
    private Player playerOne;                                                   // Erster Spieler (willkürliche Reihenfolge)
    private Player playerTwo;                                                   // Zweiter Spieler (willkürliche Reihenfolge)

    /**
     * Gib ersten Spieler zurück
     * @return Player-Objekt
     */
    @XmlElement(name = "PlayerOne")                                             // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * Setze ersten Spieler
     * @param playerOne Player-Objekt
     */
    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    /**
     * Gib zweiten Spieler zurück
     * @return Player-Objekt
     */
    @XmlElement(name = "PlayerTwo")                                             // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * Setze zweiten Spieler
     * @param playerTwo Player-Objekt
     */    
    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }
    
    /**
     * Parameterloser Standardkonstruktor (wird zwingend benötigt)
     */    
    public RockPaperScissorsParticipantsMessage(){                              // Keine weiteren Aktionen notwendig
    }
    
    /**
     * Konstruktor mit Übergabe der Spieler
     * @param one Erster Spieler
     * @param two Zweiter Spieler
     */
    public RockPaperScissorsParticipantsMessage(Player one, Player two)
    {
        super();                                                                // Initialisiere Super-Klasse
        this.setPlayerOne(one);                                                 // Setze ersten Spieler
        this.setPlayerTwo(two);                                                 // Setze zweiten Spieler
    }
    
}
