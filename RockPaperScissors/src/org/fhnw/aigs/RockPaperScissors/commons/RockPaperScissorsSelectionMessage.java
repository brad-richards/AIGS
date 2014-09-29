package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Klasse zur Mitteilung an den Server, welches Symbol (Schere, Stein, Papier, Nichts) beim Zug gewählt wurde.<br>
 * Aus Message abgeleitete Klasse.
 * @author Raphael Stoeckli
 */
@XmlRootElement(name = "RockPaperScissorsSelectionMessage")                     // Stellt sicher, dass Daten aus dieser Klasse korrekt in XML umgesetzt und versendet werden können (Kommunikation)
public class RockPaperScissorsSelectionMessage extends Message {
    
    // VARIBELDEFINITIONEN
    private RockPaperScissorsSymbol symbol;                                     // Definition des gewählten Symbols

    
    /**
     * Gibt Symbol zurück
     * @return RockPaperScissorsSymbol-Objekt
     */
    @XmlElement(name = "Symbol")                                                // Definiert, wie dieser Parameter in XML heissen soll (muss nur bei get-Methode angegeben werden)
    public RockPaperScissorsSymbol getSymbol() {
        return symbol;
    }
 
    /**
     * Setzt Symbol
     * @param symbol RockPaperScissorsSymbol-Objekt
     */
    public void setSymbol(RockPaperScissorsSymbol symbol) {
        this.symbol = symbol;
    }
    
    /**
     * Parameterloser Standardkonstruktor (wird zwingend benötigt)
     */
    public RockPaperScissorsSelectionMessage() {                                         // Keine weiteren Aktionen notwendig
    }
    
    /**
     * Konstruktor mit Übergabe von Symbol
     * @param symbol Gewähltes Symbol
     */
    public RockPaperScissorsSelectionMessage(RockPaperScissorsSymbol symbol)
    {
      super();                                                                  // Initialisieren der Super-Klasse
      this.setSymbol(symbol);                                                   // Setzen des Symbols
    }
    
}
