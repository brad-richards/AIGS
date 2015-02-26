package org.fhnw.aigs.commons;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is to route the logging of the AIGS Commons project<br>
 * If an instance of AIGS Commons is running on the AIGS server, all log messages will be suppressed to relieve the server-side logging system.<br>
 * Otherwise, a standard logger will be used
 * 
 * @author Raphael Stoeckli (26.02.2015)
 * @version v1.0
 */
public class LogRouter {
    
    /**
     * Method to route logs. If an instance of AIGS Commons is running on the AIGS server, all log messages will be suppressed to relieve the server-side logging system.<br>
     * Otherwise, a standard logger will be used
     * @param classname Name of the calling class
     * @param level Level of the log message
     * @param message Message as text (can be NULL)
     * @param arg Single argument as object
     */
    public static void log(String classname, Level level, String message, Object arg)
    {
        Package p = Package.getPackage("org.fhnw.server.common");
        if (p != null) // Only log if not running on server. User server-side LogRouter to manage logging on server
        {
           Logger.getLogger(classname).log(level, message, arg);
        }
    }

    /**
     * Method to route logs. If an instance of AIGS Commons is running on the AIGS Server, all Log messages will be suppressed to relieve the server-side logging system.<br>
     * Otherwise, a standard logger will be used
     * @param classname Name of the calling class
     * @param level Level of the log message
     * @param message Message as text (can be NULL)
     * @param args Multiple arguments as array of object
     */    
    public static void log(String classname, Level level, String message, Object[] args)
    {
        Package p = Package.getPackage("org.fhnw.server.common");
        if (p != null) // Only log if not running on server. User server-side LogRouter to manage logging on server
        {
           Logger.getLogger(classname).log(level, message, args);
        }
    }
    
}
