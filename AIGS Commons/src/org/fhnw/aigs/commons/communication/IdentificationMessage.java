package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * This message will be sent by the clients in order to identificate
 * /authentificate. The user sends his or her user name and a corresponding
 * identificationCode. The server will then checks the user name against the
 * identification code and other factors (such as unique user name), then a
 * corresponding {@link IdentificationResponseMessage} will be sent.<br>
 * There is a test user called "test" having the
 * {@link IdentificationMessage#identificationCode} "1".
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "IdentificationMessage")
public class IdentificationMessage extends Message {

    /**
     * The (proposed) user name, e.g. "pascal.fischer".
     */
    private String userName;
    /**
     * The identification code (provided). It can be seen in the server's folder
     * conf/usersXml.xml.
     */
    private String identificationCode;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public IdentificationMessage() {
    }

    /**
     * Creates a new instance of IdentificationMessage.
     *
     * @param name The user's name.
     * @param identificationCode The user's identification Code (provided).
     */
    public IdentificationMessage(String name, String identificationCode) {
        this.userName = name;
        this.identificationCode = identificationCode;
    }

    /**
     * See {@link identificationCode}
     */
    @XmlElement(name = "IdentificationCode")
    public String getIdentificationCode() {
        return identificationCode;
    }

    /**
     * See {@link userName}
     */
    @XmlElement(name = "UserName")
    public String getUserName() {
        return userName;
    }

    /**
     * See {@link identificationCode}
     */
    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    /**
     * See {@link userName}
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
