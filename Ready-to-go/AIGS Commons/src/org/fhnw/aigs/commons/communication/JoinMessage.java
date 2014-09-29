package org.fhnw.aigs.commons.communication;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.GameMode;

/**
 * This message is sent to the server when a player wants to join a message.
 * Usually the "startGame" method triggers this message.
 *
 * @author Matthias Stöckli
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
     * MultiPlayer or Test.
     */
    public JoinMessage(String gameName, GameMode gameMode) {
        this.gameName = gameName;
        this.joinTime = new Date();
        this.gameMode = gameMode;
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
     * MultiPlayer or Test.
     */
    public JoinMessage(String gameName, GameMode gameMode, String partyName) {
        this(gameName, gameMode);
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
}
