package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * This message is sent to the client as a reply to a
 * {@link IdentificationMessage}. It contains details about the success of the
 * log in process.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "IdentificationResponseMessage")
public class IdentificationResponseMessage extends Message {

    /**
     * The definite user name which will be used by the client and the server
     */
    private String userName;
    /**
     * The identification code sent by the client
     */
    private String identificationCode;
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
     * the user's name, the identificationCode, the failing reason (if there is
     * any).
     *
     * @param userName The user's definite name (may be different from the
     * proposed one)
     * @param identificationCode The user's identification code.
     * @param reason The reason why the log in failed (can be null)
     * @param loginSuccessful Flag that shows whether the login was successful
     * or not
     */
    public IdentificationResponseMessage(String userName, String identificationCode, String reason, boolean loginSuccessful) {
        this.userName = userName;
        this.identificationCode = identificationCode;
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
     * See {@link IdentificationResponseMessage#userName}.
     */
    @XmlElement(name = "UserName")
    public String getUserName() {
        return userName;
    }

    /**
     * See {@link IdentificationResponseMessage#identificationCode}.
     */
    @XmlElement(name = "IdentificationCode")
    public String getIdentificationCode() {
        return identificationCode;
    }

    /**
     * See {@link IdentificationResponseMessage#loginSuccessful}.
     */
    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    /**
     * See {@link IdentificationResponseMessage#userName}.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * See {@link IdentificationResponseMessage#reason}.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * See {@link IdentificationResponseMessage#identificationCode}.
     */
    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }
}
