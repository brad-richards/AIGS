package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * This message will be sent by the clients in order to identificate
 * /authentificate. The user sends his or her user name and a corresponding
 password. The server will then checks the user name against the
 identification code and other factors (such as unique user name), then a
 corresponding {@link IdentificationResponseMessage} will be sent.<br>
 * There is a test user called "test" having the
 * {@link IdentificationMessage#password} "1".<br>
 * v1.0 Initial release<br>
 * v1.1 added login name (split up of name into display and login name)
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.1 (Raphael Stockli, 20.10.2014)
 */
@XmlRootElement(name = "IdentificationMessage")
public class IdentificationMessage extends Message {

    /**
     * The (proposed) user name, e.g. "pascal.fischer". If login is disabled on server, this name can be arbitrary
     */
    private String loginName;
    /**
     * The identification code / password (provided). It can be seen in the server's folder
     * conf/usersXml.xml. If login is disabled, this value can be empty.
     */
    private String password;
    
    /**
     * The displayed name of the player
     */
    private String playerName;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public IdentificationMessage() {
    }

    /**
     * Creates a new instance of IdentificationMessage.
     *
     * @param loginName The user's loginName
     * @param playerName The user displayed name
     * @param password The user's password
     */
    public IdentificationMessage(String loginName, String password, String playerName) {
        this.loginName = loginName;
        this.playerName = playerName;
        this.password = password;
    }

    /**
     * See {@link password}
     */
    @XmlElement(name = "Password")
    public String getPassword() {
        return password;
    }

    /**
     * See {@link loginName}
     */
    @XmlElement(name = "LoginName")
    public String getLoginName() {
        return loginName;
    }
    
    /**
     * See {@link playerName}
     */
    @XmlElement(name = "PlayerName")
    public String getPlayerName() {
        return playerName;
    }

    /**
     * See {@link password}
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * See {@link loginName}
     */
    public void setLoginName(String userName) {
        this.loginName = userName;
    }
    
     /**
     * See {@link playerName}
     */   
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
                
    }
}
