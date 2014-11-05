package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;

/**
 * This is a Message to show the Feedback of a joining (or creating) request 
 * from the AIGS server
 * @author Raphael Stoeckli
 * @version 1.0 (22.10.2014)
 */
@XmlRootElement(name = "JoinResponseMessage")
public class JoinResponseMessage extends Message{
    
    /**
     * The message to show
     */
    private String message;
    
    /**
     * The requested type of joining or creating a game
     */
    private JoinType joinType;
    
    /**
     * The result of the requested joining operation
     */
    private boolean joinState;
    
    /**
     * True if a game was created, otherwise false
     */
    private boolean gameCreated ;

    /**
     * The game mode of the party
     */
    private GameMode gameMode;
   

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public JoinResponseMessage() {
    }

    /**
     * Creates a new instance of JoinResponseMessage,
     *
     * @param message The message to show
     * @param state The state of the joining operation
     * @param type The type of the joining operation
     * @param created True if a game was created
     * @param mode The game mode of the party
     */
    public JoinResponseMessage(JoinType type, GameMode mode, boolean state, boolean created, String message) {
        this.message = message;
        this.joinState = state;
        this.joinType = type;
        this.gameCreated = created;
        this.gameMode = mode;
    }
    
    /**
     * Creates a new instance of JoinResponseMessage without text,
     *
     * @param state The state of the joining operation
     * @param type The type of the joining operation
     * @param created True if a game was created
     */
    public JoinResponseMessage(JoinType type, boolean state, boolean created) {
        this.joinState = state;
        this.joinType = type;
        this.gameCreated = created;
    }    

    /**
     * See {@link JoinResponseMessage#gameCreated}.
     */    
    @XmlElement(name = "GameCreated")
    public boolean isGameCreated() {
        return gameCreated;
    }
    
    /**
     * See {@link JoinResponseMessage#gameCreated}.
     */  
    public void setGameCreated(boolean gameCreated) {
        this.gameCreated = gameCreated;
    }
    
    /**
     * See {@link JoinResponseMessage#message}.
     */
    @XmlElement(name = "Message")
    public String getMessage() {
        return message;
    }

    /**
     * See {@link JoinResponseMessage#message}.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * See {@link JoinResponseMessage#joinType}.
     */    
    @XmlElement(name = "JoinType")
    public JoinType getJoinType() {
        return joinType;
    }

     /**
     * See {@link JoinResponseMessage#joinType}.
     */    
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

     /**
     * See {@link JoinResponseMessage#joinState}.
     */       
    @XmlElement(name = "JoinState")
    public boolean getJoinState() {
        return joinState;
    }

     /**
     * See {@link JoinResponseMessage#joinState}.
     */         
    public void setJoinState(boolean joinState) {
        this.joinState = joinState;
    }    
    
     /**
     * See {@link JoinResponseMessage#gameMode}.
     */            
    @XmlElement(name = "GameMode")
    public GameMode getGameMode() {
        return gameMode;
    }

     /**
     * See {@link JoinResponseMessage#gameMode}.
     */       
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }    
    
}
