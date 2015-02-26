package org.fhnw.aigs.server.common;

/**
 * Enum to define the threshold of logging
 * @version 1.0
 * @author Raphael Stoeckli (26.02.2015)
 */
public enum LoggingThreshold {
    /**
     * Logging is disabled
     */
    off("Off"),
    /**
     * All messages will be logged
     */
    all("All Messages"),
    /**
     * Only warnings and severe will be logged
     */
    waringSevere("Warnings and Severe"),
    /**
     * Only severe will be logged
     */
    severe("Only Severe"),
    /**
     * Only messges from games (on the AIGS server) will be logged
     */
    game("Only Game"),
    /**
     * Severe, warnings and system messages (form the AIGS server) will be logged
     */
    warningSevereSystem("Warning, Severe and System"),
    /**
     * Severe, warnings, system messages and messages from games (on the AIGS server) will be logged
     */
    warningSevereSystemGame("Warning, Severe, System and Game"),
    /**
     * Only severe and system messages (form the AIGS server) will be logged
     */
    severeSystem("Severe and System"),
    /**
     * Severe, system messages and messages from games (on the AIGS server) will be logged
     */
    severeSystemGame("Severe, System and Game"),
    /**
     * Only system messages (form the AIGS server) will be logged
     */
    system("Only System");

     /**
     * Identifier of the enum value
     */
    private final String identifier;
    
    /**
     * Constructor of the enum
     * @param identifier Identifier string
     */    
    private LoggingThreshold(String identifier)
    {
        this.identifier = identifier;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return identifier;
    }       
    
}
