package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Enumerator für Gewähltes Symbol (Schere, Stein, Papier, Nichts)
 * @author Raphael Stoeckli
 * @version v1.0
 */
@XmlRootElement(name = "RockPaperScissorsSymbol")                               // Stellt sicher, dass Daten aus diesem Enumerator korrekt in XML umgesetzt und versendet werden können (Kommunikation)
public enum RockPaperScissorsSymbol {
    None,                                                                       // (noch) nichts gewählt
    Rock,                                                                       // Stein
    Paper,                                                                      // Papier
    Scissors,                                                                   // Schere                                               
    
}
