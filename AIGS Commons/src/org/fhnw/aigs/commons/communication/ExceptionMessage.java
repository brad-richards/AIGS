package org.fhnw.aigs.commons.communication;

import javax.xml.bind.annotation.*;

/**
 * The server sends this message to a player who caused an exception on the
 * server side. It will allow the player to track down the error.
 *
 * @author Matthias Stöckli
 */
@XmlRootElement(name = "ExceptionMessage")
public class ExceptionMessage extends Message {

    /**
     * The exception that occured (cannot be sent via XML)
     */
    private Exception exception;
    /**
     * The StackTraceElements of the exception.
     */
    private StackTraceElement[] stackTraceElements;
    /**
     * The StackTraceElements represented as strings.
     */
    private String[] stackTraceElementStrings;

    /**
     * Empty constructor. This is needed for JAXB parsing.
     */
    public ExceptionMessage() {
    }

    /**
     * Creates a new instance of ExceptionMessage.
     *
     * @param exception The exception which occured.
     */
    public ExceptionMessage(Exception exception) {
        this.exception = exception;
        this.stackTraceElements = exception.getStackTrace();
        this.stackTraceElementStrings = new String[stackTraceElements.length];
        // Transform the exception to strings.
        for (int i = 0; i < stackTraceElementStrings.length; i++) {
            stackTraceElementStrings[i] = stackTraceElements[i].toString();
        }
    }

    /**
     * See {@link exception}
     */
    @XmlTransient
    public Exception getException() {
        return this.exception;
    }

    /**
     * See {@link exception}
     */
    public void setException(Exception exception) {
        this.exception = exception;
        this.stackTraceElements = exception.getStackTrace();
    }

    /**
     * See {@link stackTraceElementStrings}
     */
    @XmlElementWrapper(name = "StackTrace")
    @XmlElement(name = "StackTraceElement")
    public String[] getStackTraceElementStrings() {
        return stackTraceElementStrings;
    }
}
