package org.fhnw.aigs.server.common;

/**
 * Enum to define the level of logging
 * @version 1.0
 * @author Raphael Stoeckli (26.02.2015)
 */
public enum LoggingLevel {
    
    /**
     * Logging is disabled
     */
    none,
    /**
     * Warning messge
     */
    waring,
    /**
     * Severe messages (serious errors)
     */
    severe,
    /**
     * Status messages from the AIGS server
     */
    system,
    /**
     * Informative messages (verbose). Only use this for debugging
     */
    info,
    /**
     * Status messages from games, running on the AIGS server
     */
    game,
}
