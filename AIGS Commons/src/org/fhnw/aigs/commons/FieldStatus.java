package org.fhnw.aigs.commons;

/**
 * This is a general purpose enumeration.<br>
 * It can be used to indicate whether a turn was successful or not etc. The
 * message {@link org.fhnw.aigs.commons.communication.FieldClickFeedbackMessage} makes use of FieldStatus.
 *
 * @author Matthias Stöckli
 * @version v1.0
 */
public enum FieldStatus {

    OK,
    Blocked,
    NoChange,
    Error,
    Warning
}
