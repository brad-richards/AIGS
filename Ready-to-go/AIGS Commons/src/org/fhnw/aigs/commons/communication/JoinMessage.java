package org.fhnw.aigs.commons.communication;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;

/**
 * This message is sent to the server when a player wants to join a message.
 * Usually the "startGame" method triggers this message.<br>
 * v1.0 Initial release<br>
 * v1.1 {@link JoinType} added
 *
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 21.10.2014)
 */
@XmlRootElement(name = "JoinMessage")
public class JoinMessage extends Message {

    /**
     * The name of the game as a String, e.g. "TicTacToe"
     */
    private String gameName;
    /**
     * The time the player sent the join request to a game
     */
    private Date joinTime;
    /**
     * The game mode which can either be SinglePlayer, Multiplayer or Test. This
     * attribute can be used to control the game's logic.
     */
    private GameMode gameMode;
    /**
     * If desired, a player can name the party he wants so play in. So he or she
     * can make sure that he will start a game with those people who are also
     * willing to join the game.
     */
    private String partyName;
    
    /**
     * The type of joining a game. The server will interprete this value and:<br>
     * a) join a random waiting game or create a new one (Auto)<br>
     * b) Create an new private game with the party name (CreateNewPrivateGame)<br>
     * c) Create an new public game with the party name (CreateNewGame)<br>
     * d) Join a particular game with the defined party name (JoinParticularGame)
     * @since v1.1
     */
    private JoinType joinType;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public JoinMessage() {
    }

    /**
     * Constructor used to generate a JoinMessage without any preferences. This
     * message will be interpreted by the server. The server will start a new
     * game or join an existing game based on the JoinMessage.
     *
     * @param gameName Name of the game, e.g. TicTacToe
     * @param gameMode The {@link GameMode}. It can either be SinglePlayer,
     * @param joinType The {@link JoinType}
     * MultiPlayer or Test.
     */
    public JoinMessage(String gameName, GameMode gameMode, JoinType joinType) {
        this.gameName = gameName;
        this.joinTime = new Date();
        this.gameMode = gameMode;
        this.joinType = joinType;
        this.partyName = "Party";
    }

    /**
     * Constructor used to generate a JoinMessage with a party preferences. This
     * message will be interpreted by the server. The server will start a new
     * game or join an existing game based on the JoinMessage. The player will
     * only be joined with other players using the same
     * {@link JoinMessage#partyName} in their JoinMessages.
     *
     * @param gameName Name of the game, e.g. TicTacToe
     * @param gameMode The {@link GameMode}. It can either be SinglePlayer,
     * @param partyName The name of the party
     * @param joinType The {@link JoinType}
     * MultiPlayer or Test.
     */
    public JoinMessage(String gameName, GameMode gameMode, String partyName, JoinType joinType) {
        this(gameName, gameMode, joinType);
        this.partyName = partyName;
    }

    /**
     * See {@link gameMode}.
     */
    @XmlElement(name = "GameMode")
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * See {@link gameMode}.
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * See {@link joinTime}.
     */
    @XmlElement(name = "JoinTime")
    public Date getJoinTime() {
        return joinTime;
    }

    /**
     * See {@link gameName}.
     */
    @XmlElement(name = "GameName")
    public String getGameName() {
        return gameName;
    }

    /**
     * See {@link partyName}.
     */
    @XmlElement(name = "PartyName")
    public String getPartyName() {
        return partyName;
    }
    
    /**
     * See {@link joinType}.
     */    
    @XmlElement(name = "JoinType")
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * See {@link joinTime}.
     */
    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    /**
     * See {@link gameName}.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * See {@link partyName}.
     */
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    /**
     * See {@link joinType}.
     */    
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }        
    
}
