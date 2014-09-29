package org.fhnw.aigs.RockPaperScissors.server;

import java.util.ArrayList;
import org.fhnw.aigs.RockPaperScissors.commons.GameState;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSelectionMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsParticipantsMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsResultMessage;
import org.fhnw.aigs.RockPaperScissors.commons.RockPaperScissorsSymbol;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.communication.GameEndsMessage;
import org.fhnw.aigs.commons.communication.Message;

/**
 * Serverseitiges Logik des Spiels. Hier werden alle Resultate verarbeitet und an die Clients zurückgeschickt.<br>
 * Aus Game abgeleitete Klasse.
 * @author Raphael Stoeckli
 */
public class GameLogic extends Game {
    
    // KONSTANTEN
    public static final String GAMENAME = "RockPaperScissors";                  // Name des Spiels, definiert als Konstante. Um Client- von Server-Logik zu trennen wird nicht der Wert aus Main.GAMENAME verwendet
    public static final int MINNUMBEROFPLAYERS = 2;                             // Minimale Anzahl von Spielern für eine Partie als Konstante
    public static final int NUMBEROFTURNS = 3;                                  // Anzahl Züge, bis zu Spielende als Konstante
    
    // VARIBELDEFINITIONEN    
    private ArrayList<RockPaperScissorsTurn> turnPlayers;                       // Liste aller Spieler mit den jeweiligen Turn-Objekten. Das Turn-Objekt stellt den aktuellen Zug des Spielers dar
    private int turnNumber;                                                     // Nummer des Zuges
    private boolean lastTurn;                                                   // Angabe, ob es sich um den letzten Zug handelt
    
    
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
    @Override                                                                   //  Implementierte Methode
    public void initialize() {
       turnPlayers = new ArrayList<>();                                         // Initialisieren der Turn-Liste (i.d. Regel nur zwei Elemente)
       RockPaperScissorsTurn turn = null;                                       // Temporäres Turn-Objekt definieren...
       for(int i = 0; i < players.size(); i++)                                  // Gehe durch alle Spieler (normalerweise nur zwei)
       {
           turn = new RockPaperScissorsTurn(players.get(i));                    // ... und neues Turn-Objekt mit Spieler erstellen
           turnPlayers.add(turn);                                               // Füge Turn-Objekt der Liste hinzu
       }
       turnNumber = 1;                                                          // Aktuell: 1. Zug
       lastTurn = false;                                                        // Nicht der letzte Zug
       
       // WICHTIG!!!
       setCurrentPlayer(getRandomPlayer());                                     // Zwingend Notwendig um das Spiel zu starten. Die Reihenfolge der Spieler ist bei diese Spiel nicht wichtig. Die Methode muss einfach für einen korrekten Programmablauf aufgerufen werden, hat aber keinen Einfluss auf den Spielverlauf
       startGame();                                                             // Startes die Spielpartie (Methode aus Super-Klasse)
       
                                                                                // Erstelle neue nachricht zur Mitteilung aller Spieler der Partie
        RockPaperScissorsParticipantsMessage identification = new RockPaperScissorsParticipantsMessage(turnPlayers.get(0).getPlayer(), turnPlayers.get(1).getPlayer());
        sendMessageToAllPlayers(identification);                                // Sende an alle Spieler (2), wer in der Partie mitspielt
        // Das Spiel ist jetzt bereit und wartet auf den ertsen Zug
    }

    /**
     * Methode verarbeitet die Nachrichten von Clients. Die Methode wird automatisch beim Eintreffen einer Nachricht aufgerufen.
     * @param msg Nachricht vom Client. Nachrichten müssen als abgeleitete Klassen im Package commons vorhanden sein
     * @param player Spieler, welcher die Nachricht geschickt hat
     */
    @Override                                                                   // Implementierte Methode
    public void processGameLogic(Message msg, Player player) {
        if (msg instanceof RockPaperScissorsSelectionMessage) {                 // Wenn Message vom Typ RockPaperScissorsSelectionMessage...
  
            if (turnNumber == NUMBEROFTURNS)                                    // Wenn Nummer des aktuellen Zuges == Maximale Anzahl Züge...
            {
                lastTurn = true;                                                // Booelan auf True setzen (letzter Zug erreicht)
            }
                
            RockPaperScissorsSelectionMessage castedMsg = (RockPaperScissorsSelectionMessage)msg; // Nachricht ins richtige Format casten
            for(int i = 0; i < turnPlayers.size(); i++)                         // Gehe alle Spieler (Turns) durch
            {
                if (turnPlayers.get(i).getPlayerID() == player.getId())         // Ermittle den Spieler dieser Nachricht
                {
                    turnPlayers.get(i).setTurnSymbol(castedMsg.getSymbol());    // Setze symbol des Spielers
                    turnPlayers.get(i).setTurnFinished(true);                   // Setze Status (Zug beendet = true)
                    break;                                                      // schleife Verlassen, da sowieso nur ein Spieler verarbeitet wird
                }
            }
            // Eigentliche Prüfung der Konditionen erfolgt in Methode: checkForWinningCondition (wird bei jedem Zug nach den Messages geprüft)
        }
    }

    /**
     * Methode zum Überprüfen, ob die Partie mit aktuellem Zug beendet wird.<br>
     * Die Methode wird bei jedem Zug, nach Verarbeitung der Messages geprüft.
     */
    @Override                                                                   //  Implementierte Methode
    public void checkForWinningCondition() {
            if (allPlayersFinished() == true)                                   // Prüfe, ob jetzt alle Player den Zug beendet haben. Wenn false, wird die Methode verlassen
            {
                int winnerIndex = calculateTurnWinner();                        // Berechne den Gewinner (-1 = Unentschieden)
                RockPaperScissorsResultMessage result = null;                   // Definiere neue Antwort-Nachricht
                String messageText = "";                                        // Definiere Nachrichtentext
                RockPaperScissorsTurn me = null;                                // Definiere neues, temporäres Objekt für den Turn des Spielers (an welchen die Nachricht gesendet wird)
                RockPaperScissorsTurn opponent = null;                          // Definiere neues, temporäres Objekt für den Turn des Gegners (des Spielers, an welchen die Nachricht gesendet wird)
                    for(int i = 0; i < turnPlayers.size(); i++)                 // Gehe alle Spieler (Turns) durch
                    {
                       me = turnPlayers.get(i);                                 // Objkt des Spielers (Nachricht geht an diesen)
                       opponent = turnPlayers.get(me.getOpponentIndex());       // Objekt des Gegners
                       if (winnerIndex == -1)                                   // Wenn unentschieden...
                       {
                           messageText = "Zwei mal " + GameLogic.printSymbol(me.getTurnSymbol()) + ". Unentschieden!"; // Setze Nachrichtentext
                       }
                       else if (i == winnerIndex)                               // Wenn Gewinner = Spieler (me)...
                       {                                                        // Setze Nachrichtentext
                           messageText = GameLogic.printSymbol(me.getTurnSymbol()) + " schlägt " + GameLogic.printSymbol(opponent.getTurnSymbol()) + ". " + me.getPlayerName() + " gewinnt!";
                       }
                       else                                                     // Wenn Gewinner = Gener (opponent)
                       {                                                        // Setze Nachrichtentext
                           messageText = GameLogic.printSymbol(opponent.getTurnSymbol()) + " schlägt " + GameLogic.printSymbol(me.getTurnSymbol()) + ". " + opponent.getPlayerName() + " gewinnt!";
                       }
                           result = new RockPaperScissorsResultMessage(         // Erstelle Nachricht mit allen Parametern (Zur besseren Darstellung auf mehreren Zeilen)
                                   me.getTurnState(), 
                                   lastTurn, 
                                   me.getTurnSymbol(),
                                   opponent.getTurnSymbol(),
                                   opponent.getPlayerName(),
                                   me.getPoints(),
                                   opponent.getPoints(),
                                   turnNumber,
                                   messageText
                           ); 
                     
                        sendMessageToPlayer(result, me.getPlayer());            // Sende Nachricht an Spieler (me)
                    }
                    for(int i = 0; i < turnPlayers.size(); i++)                 // Zurücksetzen der Partie für nächsten Zug (bei allen Spielern)
                    {
                        turnPlayers.get(i).nextTurn();                          // Zurücksetzen des Spielers in der ArrayList mit dem index i
                    }
                    turnNumber++;                                               // Zähle die Nummer des Zugs hoch
                    if (lastTurn == true)
                    {                                                           // Wenn letzer Zug erreicht wurde
                        RockPaperScissorsTurn winner = calculateGameWinner();   // Berechnen des Gesamtgewinners
                        GameEndsMessage endMessage = null;                      // Definition der Nachricht zum Beenden des Spiels
                        if (winner == null)                                     // Wenn unentschieden (über alle Züge)...
                        {
                            endMessage = new GameEndsMessage("Unentschieden!"); // Setze Nachricht für Spielende
                        }
                        else                                                    // Wenn nicht unentschieden...
                        {
                                                                                // Setze Nachricht für Spielende
                            endMessage = new GameEndsMessage(winner.getPlayerName() + " hat mit " + Integer.toString(winner.getPoints()) + " Zügen gewonnen.");
                        }
                        sendMessageToAllPlayers(endMessage);                    // Sende Nachricht zu allen Spielern (das Spiel ist danach zu ende)                        
                    }
            }
    }
    
    /**
     * Methode ermittelt ob alle Spieler ihren Zug gemacht haben
     * @return Gibt false zurück, wenn mindestens ein Spieler noch nicht gewählt hat
     */
    private boolean allPlayersFinished()
    {
        boolean finished = true;                                                // Standard: Alle Spieler fertig
        for(int i = 0; i < turnPlayers.size(); i++)                             // Gehe alle Spieler durch
        {
            if (turnPlayers.get(i).hasTurnFinished()== false)                   // Wenn Spieler mit Index i noch nicht beendet hat...
            {
                finished = false;                                               // Boolean auf false = nicht fertig 
                break;                                                          // Schleife verlassen, da Resultat bereits bekannt
            }
        }
        return finished;                                                        // Gebe Resultat zurück
    }
    
    /**
     * Methode berechnet, wer gewonnen hat und gibt Index der ArrayListe zurück.<br>Momentan werden nur zwei Spieler vearbeitet
     * @return Index der ArrayListe, welche auf den Gewinner zeigt
     */
    private int calculateTurnWinner()
    {
        int winnerIndex = -1;                                                   // -1 bedeutet Draw (Unentschieden)
        for(int i = 0; i < turnPlayers.size(); i++)                             // Könnte theoretisch auf mehr als 2 Spieler augebaut werden
        {
            for(int j = i+1; j < turnPlayers.size(); j++)                       // Könnte theoretisch auf mehr als 2 Spieler augebaut werden
            {
                if (turnPlayers.get(i).getTurnSymbol() == turnPlayers.get(j).getTurnSymbol()) // Wenn unentschieden...
                {
                    turnPlayers.get(i).setTurnState(GameState.Draw);            // Setze bei beiden Spielern auf unentschieden
                    turnPlayers.get(j).setTurnState(GameState.Draw);            // "
                }                                                               // Wenn Spieler mit Index i Papier und Spieler mit Index j Stein gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Paper && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Rock)
                {
                    turnPlayers.get(i).setTurnState(GameState.Win,1);           // Trage Spieler mit Index i als Gewinner ein
                    turnPlayers.get(j).setTurnState(GameState.Lose);            // Trage Spieler mit Index j als Verlierer ein        
                }                                                               // Wenn Spieler mit Index i Papier und Spieler mit Index j Schere gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Paper && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Scissors)
                {
                    turnPlayers.get(i).setTurnState(GameState.Lose);            // Trage Spieler mit Index i als Verlierer ein
                    turnPlayers.get(j).setTurnState(GameState.Win,1);           // Trage Spieler mit Index j als Gewinner ein                     
                }                                                               // Wenn Spieler mit Index i Stein und Spieler mit Index j Papier gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Rock && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Paper)
                {
                    turnPlayers.get(i).setTurnState(GameState.Lose);            // Trage Spieler mit Index i als Verlierer ein
                    turnPlayers.get(j).setTurnState(GameState.Win,1);           // Trage Spieler mit Index j als Gewinner ein                     
                }                                                               // Wenn Spieler mit Index i Stein und Spieler mit Index j Schere gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Rock && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Scissors)
                {
                    turnPlayers.get(i).setTurnState(GameState.Win,1);           // Trage Spieler mit Index i als Gewinner ein
                    turnPlayers.get(j).setTurnState(GameState.Lose);            // Trage Spieler mit Index j als Verlierer ein                     
                }                                                               // Wenn Spieler mit Index i Schere und Spieler mit Index j Stein gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Scissors && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Rock)
                {
                    turnPlayers.get(i).setTurnState(GameState.Lose);            // Trage Spieler mit Index i als Verlierer ein
                    turnPlayers.get(j).setTurnState(GameState.Win,1);           // Trage Spieler mit Index j als Gewinner ein                     
                }                                                               // Wenn Spieler mit Index i Schere und Spieler mit Index j Papier gewählt hat...
                else if (turnPlayers.get(i).getTurnSymbol() == RockPaperScissorsSymbol.Scissors && turnPlayers.get(j).getTurnSymbol() == RockPaperScissorsSymbol.Paper)
                {
                    turnPlayers.get(i).setTurnState(GameState.Win,1);           // Trage Spieler mit Index i als Gewinner ein
                    turnPlayers.get(j).setTurnState(GameState.Lose);            // Trage Spieler mit Index j als Verlierer ein                     
                }
                turnPlayers.get(i).setOpponentIndex(j);                         // Trage Index (ArrayList) des Gegners mit dem Index i ein
                turnPlayers.get(j).setOpponentIndex(i);                         // Trage Index (ArrayList) des Gegners mit dem Index j ein
            }    
        }
        for(int i = 0; i < turnPlayers.size(); i++)                             // Suche Gewinner
        {
          if (turnPlayers.get(i).getTurnState() == GameState.Win)               // Gewinner gefunden (bei Draw wind nichts gefunden und der Index bleibt -1)
          {
              winnerIndex = i;                                                  // Setze Index (ArrayList) des Gewinners
              break;                                                            // Unterbreche Schleife, da nur ein Gewinner möglich (bei zwei Spielern)
          }
        }
        return winnerIndex;                                                     // Gebe Index (ArrayListe) des Gewinners zurück
    }
    
    /**
     * Mthode gibt Turn-Objekt des Gewinners des ganzen Spiels zurück. Falls es keinen Gewinner gibt (unentschieden/draw), wird null zurückgegeben
     * @return RockPaperScissorsTurn-Objekt des Gewinners oder null, wenn unentschieden
     */
    private RockPaperScissorsTurn calculateGameWinner()
    {
        int winnerIndex = 0;                                                    // Setze vorerst ersten Spieler der Liste als Gewinner
        int maxPoints = turnPlayers.get(0).getPoints();                         // Setze vorerst Punktzahl des ersten Spieler der Liste als höchste Punktzahl 
        boolean draw = true;                                                    // Setze vorerst unentschieden auf true (wird sich bei identsichen Punktzahlen nicht ändern)
        for(int i = 1; i < turnPlayers.size(); i++)                             // Gehe Spieler durch, beginne aber beim 2. Spieler (i = 1 und nicht i = 0)
        {
            if (turnPlayers.get(i).getPoints() > maxPoints)                     // Wenn Punktzahl des Spielers mit index i grösser als maximale Punktzahl...
            {
                winnerIndex = i;                                                // Setze neuen Index des Gewinners
                maxPoints = turnPlayers.get(i).getPoints();                     // Setze neue maximale Punktzahl
                draw = false;                                                   // Setze unentschieden auf false
            }
            else if (turnPlayers.get(i).getPoints() < maxPoints)                // Wenn Punktzahl des Spielers mit index i kleiner als maximale Punktzahl...
            {
                draw = false;                                                   // Setze unentschieden auf false
            }
        }
        if (draw == true)                                                       // Wenn untentschieden...
        {
            return null;                                                        // gebe null zurück
        }
        else                                                                    // Wen nicht unentschieden...
        {
            return turnPlayers.get(winnerIndex);                                // Gebe Objekt (Turn) des Gewinners zurück
        }
    }
    
    /**
     * Methode gibt einen String des gewählten Symbols aus
     * @param symbol Symbol (Schere, Stein, Papier) als Enumerator-Wert
     * @return String des Enumerator-Werts
     */
    public static String printSymbol(RockPaperScissorsSymbol symbol)
    {
        if (symbol == RockPaperScissorsSymbol.Paper)                            // Wenn Symbol Papier...
        {
            return "Papier";
        }
        else if (symbol == RockPaperScissorsSymbol.Rock)                        // Wenn Symbol Stein...
        {
            return "Stein";
        }
        else if (symbol == RockPaperScissorsSymbol.Scissors)                    // Wenn Symbol Schere...
        {
            return "Schere";
        }
        else                                                                    // Fallback: Wenn None übergeben wurde
        {
            return "Nichts";
        }
    }
    
}
