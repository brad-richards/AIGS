package org.fhnw.aigs.server.common;

/**
 * Enum to define the Style of logging
 * @version 1.0
 * @author Raphael Stoeckli (26.02.2015)
 */
public enum LoggingStyle {

    /**
     * Full (uncompressed) XML logging
     */
    xmlFull("XML"),
    /**
     * Compact XML logging
     */
    xmlCompact("XML Compact"),
    /**
     * Plain text (uncompressed) logging
     */
    plainFull("Plain"),
    /**
     * Compact plain text logging
     */
    plainCompact("Plain Compact"),
    /**
     * Most compact plain text logging
     */
    compressed("Compressed"),
    /**
     * All messages will be discared. Use this to detect logging events without recording the actual messages
     */
    discard("Discard");

    /**
     * Identifier of the enum value
     */
    private final String identifier;
    
    /**
     * Constructor of the enum
     * @param identifier Identifier string
     */
    private LoggingStyle(String identifier)
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
