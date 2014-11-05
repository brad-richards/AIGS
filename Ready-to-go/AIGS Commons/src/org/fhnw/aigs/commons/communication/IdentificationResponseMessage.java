package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * This message is sent to the client as a reply to a
 * {@link IdentificationMessage}. It contains details about the success of the
 * log in process.<br>
 * v1.0 Initial release<br>
 * v1.1 added login name (split up of name into display and login name)
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.1 (Raphael Stoeckli, 23.10.2014)
 */
@XmlRootElement(name = "IdentificationResponseMessage")
public class IdentificationResponseMessage extends Message {

    /**
     * The definite login name if login is enabled on the server
     */
    private String loginName; 
    /**
     * The displayed player name
     */
    private String playerName;
    /**
     * The password sent by the client
     */
    private String password;
    /**
     * The reason (if there is any) why the log in process failed
     */
    private String reason;
    /**
     * Flag that shows whether the login was successful or not
     */
    private boolean loginSuccessful;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public IdentificationResponseMessage() {
    }

    /**
     * Creates a new instance of IdentificationResponseMessage. This will set
     * the user's name, the password, the failing reason (if there is any).
     *
     * @param loginName The user's login name
     * @param playerName The user's displayed name 
     * @param password The user's password.
     * @param reason The reason why the log in failed (can be null)
     * @param loginSuccessful Flag that shows whether the login was successful or not
     */
    public IdentificationResponseMessage(String loginName, String playerName, String password, String reason, boolean loginSuccessful) {
        this.loginName = loginName;
        this.playerName = playerName;
        this.password = password;
        this.reason = reason;
        this.loginSuccessful = loginSuccessful;
    }

    /**
     * See {@link IdentificationResponseMessage#loginSuccessful}.
     */
    @XmlElement(name = "LoginSuccessful")
    public boolean getLoginSuccessful() {
        return loginSuccessful;
    }

    /**
     * See {@link IdentificationResponseMessage#reason}.
     */
    @XmlElement(name = "Reason")
    public String getReason() {
        return reason;
    }

    /**
     * See {@link IdentificationResponseMessage#loginName}.
     */
    @XmlElement(name = "LoginName")
    public String getLoginName() {
        return loginName;
    }
    
    /**
     * See {@link IdentificationResponseMessage#playerName}.
     */
    @XmlElement(name = "PlayerName")
    public String getPlayerName() {
        return playerName;
    }    

    /**
     * See {@link IdentificationResponseMessage#password}.
     */
    @XmlElement(name = "Password")
    public String getPassword() {
        return password;
    }

    /**
     * See {@link IdentificationResponseMessage#loginSuccessful}.
     */
    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    /**
     * See {@link IdentificationResponseMessage#loginName}.
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    
    /**
     * See {@link IdentificationResponseMessage#playerName}.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }    

    /**
     * See {@link IdentificationResponseMessage#reason}.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * See {@link IdentificationResponseMessage#password}.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
