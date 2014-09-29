package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;
import org.fhnw.aigs.commons.Player;

/**
 * This message is sent by the server as soon as the player was identified, a
 * join message has been sent and a game has enough participants for a new game.
 * It will inform all clients of a game about the game. <br>
 * Additionally the "startingPlayer" indicates who is going to start the game.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "GameStartMessage")
public class GameStartMessage extends Message {

    /**
     * The current player of the game.
     */
    private Player startingPlayer;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public GameStartMessage() {
    }

    /**
     * Creates a new instance of GameStartMessage.
     *
     * @param startingPlayer
     */
    public GameStartMessage(Player startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    /**
     * See {@link GameStartMessage#startingPlayer}.
     */
    public Player getStartingPlayer() {
        return startingPlayer;
    }

    /**
     * See {@link GameStartMessage#startingPlayer}.
     */
    public void setStartingPlayer(Player startingPlayer) {
        this.startingPlayer = startingPlayer;
    }
}