package org.fhnw.aigs.RockPaperScissors.commons;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Enumerator für Zustand einer Partie
 * @author Raphael Stoeckli
 * @version v1.0
 */
@XmlRootElement(name = "GameState")                                             // Stellt sicher, dass Daten aus diesem Enumerator korrekt in XML umgesetzt und versendet werden können (Kommunikation)
public enum GameState {
    None,                                                                       // Undefiniert, noch kein Zug
    Win,                                                                        // Gewonnen
    Lose,                                                                       // Verloren
    Draw,                                                                       // Unentschieden
    
}
