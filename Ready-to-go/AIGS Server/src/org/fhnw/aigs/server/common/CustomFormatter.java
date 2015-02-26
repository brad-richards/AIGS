package org.fhnw.aigs.server.common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import sun.util.logging.LoggingSupport;

/**
 * Custom formatter for logging. This class can switch the logging style ad hoc 
 * at runtime. Implemented is XML format, simple format and a compressed format.<br>
 * Most parts of XML and simple format are copied from the original Java SE sources.
 * 
 * @version 1.0
 * @author Raphael Stoeckli (26.02.2015)
 */
public class CustomFormatter extends Formatter {

    /**
     *  Format string of simple format
     */
    private static final String format = LoggingSupport.getSimpleFormat();
    
    /**
     * Date object of logging date and time
     */
    private final Date dat = new Date();
    
    /**
     * Format object of simple SimpleDateFormat
     */
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    
    /**
     * Current logging style of the formatter
     */
    private LoggingStyle loggingStyle = LoggingStyle.plainCompact;
    
    /**
     * Getter of the logging style
     * @return Current logging style of the formatter
     */
    public LoggingStyle getLoggingStyle() {
        return loggingStyle;
    }

    /**
     * Setter of the logging style
     * @param loggingStyle Current logging style of the formatter
     */
    public void setLoggingStyle(LoggingStyle loggingStyle) {
        this.loggingStyle = loggingStyle;
    }
    
    /**
     * Constructor with definition of the logging style
     * @param style Logging style for the formatter
     */
    public CustomFormatter(LoggingStyle style)
    {
        super();
        this.loggingStyle = style;
    }    
    
    /**
     * Method to format an incoming log record according the current {@link CustomFormatter#loggingStyle}
     * @param record Log record to process
     * @return Processed log record as string
     */
    @Override
    public synchronized String format(LogRecord record) {
        if (this.loggingStyle == LoggingStyle.plainFull )
        {
            return getSimpleFormat(record);
        }
        else if (this.loggingStyle == LoggingStyle.xmlFull )
        {
            return getXmlFormat(record);
        }
        else if (this.loggingStyle == LoggingStyle.compressed )
        {
            return getCompressedFormat(record);
        }
        else
        {
            return ""; // Discard
        }
    }
    
    /**
     * Method to format an incoming log record in a compressed style
     * @param record Log record to process
     * @return Processed log record as string
     */
    private String getCompressedFormat(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();
        dat.setTime(record.getMillis());
        sb.append(dateFormat.format(dat));
        sb.append("\t");
        sb.append(record.getLevel().getLocalizedName());
        sb.append("\t");
        sb.append(formatMessage(record));
        sb.append("\n");
        return sb.toString();
    }
    
    /**
     * Method to format an incoming log record in a simple style
     * @param record Log record to process
     * @return Processed log record as string
     */
    private String getSimpleFormat(LogRecord record)
    {
         dat.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                             dat,
                             source,
                             record.getLoggerName(),
                             record.getLevel().getLocalizedName(),
                             message,
                             throwable);    
        
    }
    
    /**
     * Method to format an incoming log record in XML style
     * @param record Log record to process
     * @return Processed log record as string
     */
    private String getXmlFormat(LogRecord record) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("<record>\n");

        sb.append("  <date>");
        appendISO8601(sb, record.getMillis());
        sb.append("</date>\n");

        sb.append("  <millis>");
        sb.append(record.getMillis());
        sb.append("</millis>\n");

        sb.append("  <sequence>");
        sb.append(record.getSequenceNumber());
        sb.append("</sequence>\n");

        String name = record.getLoggerName();
        if (name != null) {
            sb.append("  <logger>");
            escape(sb, name);
            sb.append("</logger>\n");
        }

        sb.append("  <level>");
        escape(sb, record.getLevel().toString());
        sb.append("</level>\n");

        if (record.getSourceClassName() != null) {
            sb.append("  <class>");
            escape(sb, record.getSourceClassName());
            sb.append("</class>\n");
        }

        if (record.getSourceMethodName() != null) {
            sb.append("  <method>");
            escape(sb, record.getSourceMethodName());
            sb.append("</method>\n");
        }

        sb.append("  <thread>");
        sb.append(record.getThreadID());
        sb.append("</thread>\n");

        if (record.getMessage() != null) {
            // Format the message string and its accompanying parameters.
            String message = formatMessage(record);
            sb.append("  <message>");
            escape(sb, message);
            sb.append("</message>");
            sb.append("\n");
        }

        // If the message is being localized, output the key, resource
        // bundle name, and params.
        ResourceBundle bundle = record.getResourceBundle();
        try {
            if (bundle != null && bundle.getString(record.getMessage()) != null) {
                sb.append("  <key>");
                escape(sb, record.getMessage());
                sb.append("</key>\n");
                sb.append("  <catalog>");
                escape(sb, record.getResourceBundleName());
                sb.append("</catalog>\n");
            }
        } catch (Exception ex) {
            // The message is not in the catalog.  Drop through.
        }

        Object parameters[] = record.getParameters();
        //  Check to see if the parameter was not a messagetext format
        //  or was not null or empty
        if ( parameters != null && parameters.length != 0
                && record.getMessage().indexOf("{") == -1 ) {
            for (int i = 0; i < parameters.length; i++) {
                sb.append("  <param>");
                try {
                    escape(sb, parameters[i].toString());
                } catch (Exception ex) {
                    sb.append("???");
                }
                sb.append("</param>\n");
            }
        }

        if (record.getThrown() != null) {
            // Report on the state of the throwable.
            Throwable th = record.getThrown();
            sb.append("  <exception>\n");
            sb.append("    <message>");
            escape(sb, th.toString());
            sb.append("</message>\n");
            StackTraceElement trace[] = th.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement frame = trace[i];
                sb.append("    <frame>\n");
                sb.append("      <class>");
                escape(sb, frame.getClassName());
                sb.append("</class>\n");
                sb.append("      <method>");
                escape(sb, frame.getMethodName());
                sb.append("</method>\n");
                // Check for a line number.
                if (frame.getLineNumber() >= 0) {
                    sb.append("      <line>");
                    sb.append(frame.getLineNumber());
                    sb.append("</line>\n");
                }
                sb.append("    </frame>\n");
            }
            sb.append("  </exception>\n");
        }

        sb.append("</record>\n");
        return sb.toString();
    }    

    // Append to the given StringBuilder an escaped version of the
    // given text string where XML special characters have been escaped.
    // For a null string we append "<null>"
    
    /**
     * Append to the given StringBuilder an escaped version of the
     * given text string where XML special characters have been escaped. 
     * For a null string we append "&lt;null&gt;"
     * @param sb String builder object
     * @param text Given text
     */
    private void escape(StringBuilder sb, String text) {
        if (text == null) {
            text = "<null>";
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else {
                sb.append(ch);
            }
        }
    }    
    
    /**
     * Append the time and date in ISO 8601 format
     * @param sb String builder object
     * @param millis milliseconds to append
     */
    private void appendISO8601(StringBuilder sb, long millis) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millis);
        sb.append(cal.get(Calendar.YEAR));
        sb.append('-');
        a2(sb, cal.get(Calendar.MONTH) + 1);
        sb.append('-');
        a2(sb, cal.get(Calendar.DAY_OF_MONTH));
        sb.append('T');
        a2(sb, cal.get(Calendar.HOUR_OF_DAY));
        sb.append(':');
        a2(sb, cal.get(Calendar.MINUTE));
        sb.append(':');
        a2(sb, cal.get(Calendar.SECOND));
    }
    
    /**
     * Appends a two digit number.
     * @param sb String builder object
     * @param x number to append
     */
    private void a2(StringBuilder sb, int x) {
        if (x < 10) {
            sb.append('0');
        }
        sb.append(x);
    }   
    
}
