package org.fhnw.aigs.client.GUI;

/**
 * Enum to define a JavaFX Layer 
 * @version 1.0
 * @author Raphael Stoeckli (23.04.2015)
 */
public enum LayerType {
    
    
    /**
     * Undefined
     */
    none("NONE"),
    /**
     * Setup window before connecting to the server
     */
    setup("SETUP"),
    /**
     * Loading window when connection to the server
     */
    loading("LOADING"),
    /**
     * End window
     */
    end("END"),
    /**
     * Header panel
     */
    header("HEADER"),    
    /**
     * Main game board
     */    
    board("BOARD"),
    /**
     * Settings window (if invoked)
     */        
    settings("SETTINGS"),
    /**
     * Unmanaged custom panel
     */
    custom("CUSTOM");

     /**
     * Identifier of the enum value
     */
    private final String identifier;
    
    /**
     * Constructor of the enum
     * @param identifier Identifier string
     */    
    private LayerType(String identifier)
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
    
    /**
     * Method to compare a String with an enum value
     * @param type value to check
     * @param compareTerm Compare term (ID of a layer)
     * @return True if the type and compare term are matching
     */
    public static boolean CompareString(LayerType type, String compareTerm)
    {
        return compareTerm.equals(type.toString());
    }
    
    
}
