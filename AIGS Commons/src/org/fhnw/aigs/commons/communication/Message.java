package org.fhnw.aigs.commons.communication;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import org.fhnw.aigs.commons.LogRouter;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.XMLHelper;

/**
 * This is the base class for all custom Message classes. It provides an
 * interface which will be used by the mechanisms behind the AIGS and AIGS
 * BaseClient. Every message carries the information on who sent the message (in
 * the case of a client-&rarr;server message) or who will receive the message
 * (server-&rarr;client). Additionally every message carries the fully qualified name
 * of the class, e.g. "org.fhnw.aigs.commons.JoinMessage" which allows the
 * server or client to load the class.<br>
 * Every message can have as many additional attributes as necessary. They will
 * automatically be translated into XML. The marshalling and unmarshalling is
 * handled by JAXB.<br>
 * Please make sure that:<br><ul>
 * <li>Every message class must have a default, zero-argument constructor!</li>
 * <li>Every message class must be annotated with the
 *
 * {@literal @}XmlElementRoot attribute in the following
 * way: <code>@XmlRootElement(name="YourClassName")</code></li>
 * <li>Every field is set to private.</li>
 * <li>Every attribute that is not a primitive datatype <b>MUST HAVE</b> an
 * empty zero-argument constructor.</li>
 * <li>Every field possesses a getter and setter which has EXACTLY this
 * structure:
 * <b>get[CORRESPONDING ATTRIBUTE NAME]</b>, <b>set[CORRESPONDINGATTRIBUTE]</b>
 * name. So the attribute <b>isRed</b> has a setter <b>setIsRed</b> and a getter
 * <b>getIsRed</b>.<li>
 * <li>Every getter and setter is annotated with the
 * {@literal @}XmlElement annotation. The name attribute of the annotation is the name that
 * will be used when marshalled to xml. If this has not been done JAXB will
 * assume a name for the element.</li>
 * <li>Every array field must be annotated with the
 * {@literal @}XmlElementWrapper annotation. The name attribute of the annotation is the
 * parent element's name, the
 * {@literal @}XmlElement's name attribute is the child's name in the marshalled XML
 * form.</li></ul><br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Changing of logging
 * @author Matthias Stöckli (v1.0)
 * @version 1.2 (Raphael Stoeckli, 26.02.2015)
 */
public abstract class Message {

    /**
     * Every message carries the information to and from which message the
     * message will be sent.
     */
    protected Player player;

    /**
     * Every message <b>must</b> possess an empty constructor due to the
     * mechanisms of how JAXB, the framework which takes care of the XML
     * marshalling and unmarshalling
     */
    public Message() {
    }

    /**
     * Gets the fully qualified class name of the message.
     */
    @XmlAttribute(name = "FullyQualifiedClassName")
    public String getFullyQualifiedName() {
        return getClass().getCanonicalName();
    }

    /**
     * See {@link player}
     */
    @XmlElement(name = "Player")
    public Player getPlayer() {
        return player;
    }

    /**
     * See {@link player}
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
   
    /**
     * Sends a message to a player using a socket.<br>
     * Usually a call to {@link org.fhnw.aigs.commons.Game#sendMessageToPlayer} or
     * {@link org.fhnw.aigs.commons.Game#sendMessageToAllPlayers} would be the preferred method.<br>
     * Hack inserted since v1.1 &rarr; Problem with ClassLoader
     *
     * @param socket The player's socket.
     * @param player The receiving player.
     */    
    public void send(Socket socket, Player player)
    {
        // HACK: The very first call of a running game causes a crash in marshaller. All further attemps running without problems. Problem with ClassLoader???
       boolean processingError = false;
       String errorMessage = "";
       Exception ex1 = null;
       for(int i = 0; i < 5; i++)  // In case of an exception, let's try n times and then give up (and handle error message)
       {  
            try {
                // It is not possible to send a message without a socket
                if (socket == null) {
                    throw new IOException("No socket available - could not send message!");
                }
                this.player = player;
                // Marshal/turn the message into XML and create a {@link StringWriter} out of it.
                Marshaller marshaller = XMLHelper.getUnformattedXMLMarshaller(this);
                StringWriter sw = new StringWriter();
                marshaller.marshal(this, sw);

                // Write the message to a {@link OutputStreamWriter} / {@link PrintWriter}
                // and print the message to the client. Then send/flush the writer.
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                String xmlString = sw.toString();
                writer.println(xmlString);
                writer.flush();
                //Logger.getLogger(Message.class.getName()).log(Level.INFO, "=> \n {0}", XMLHelper.prettyPrintXml(xmlString));
                LogRouter.log(Message.class.getName(), Level.INFO, "=> \n {0}", XMLHelper.prettyPrintXml(xmlString));
                processingError = false;
                break;

            }
            catch (JAXBException ex)
            {
                errorMessage = "Message could not be parsed.";
                ex1 = ex;
                processingError = true;
            }
            catch (IOException ex) 
            {
                errorMessage = "Could not send the message, socket is missing. Game will be terminated.";
                ex1 = ex;
                processingError = true;           
            }
            catch (Exception ex) // All other errors
            {
                errorMessage = "An unknown error occurred.";
                ex1 = ex;
                processingError = true;           
            }
       }
       if (processingError == true) // none of the n attemps was successful
       {
           //Logger.getLogger(Message.class.getName()).log(Level.SEVERE, errorMessage, ex1);
           LogRouter.log(Message.class.getName(), Level.SEVERE, errorMessage, ex1);
       }
    }      

    /**
     * Turns a plain text xml message in a StringReader into a message object.
     * Usually it should not be necessary to call this method as the messages
     * will be parsed by the server and base clients already. However this
     * method may be used to create custom xml-parsing implementations.
     *
     * @param <T> The desired class.
     * @param reader A {@link StringReader} containing the message's text.
     * @param messageClass The desired class.
     * @return The parsed message.
     */
    public static <T extends Message> Message parse_OLD(StringReader reader, Class<T> messageClass) {
        Message parsedMessage = null;
        try {
            parsedMessage = JAXB.unmarshal(reader, messageClass);
        } catch (NoSuchMethodError ex) {
            parsedMessage =
                    new ForceCloseMessage("The class " + messageClass.getSimpleName()
                    + " is erroneous. Please check the annotations.");
        }
        return parsedMessage;

    }
    
    public static <T extends Message> Message parse(StringReader reader, Class<T> messageClass) {
       Message parsedMessage = null;
       boolean processingError = false;
       // HACK: The very first call of a running game causes a crash in unmarshal. All further attemps running without problems. Problem with ClassLoader???
       for(int i = 0; i < 5; i++)  // In case of an exception, let's try n times and then give up (and handle error message)
       {  
         try
        {
            parsedMessage = JAXB.unmarshal(reader, messageClass);
            processingError = false;
            break;
        }
        catch (NoSuchMethodError ex) 
        {
            processingError = true;
        }
         catch(Exception ex) // All other exceptions
         {
             processingError = true;
         }
       }        
        if (processingError == true)
        {
            parsedMessage =  new ForceCloseMessage("The class " + messageClass.getSimpleName() + " is erroneous. Please check the annotations.");
        }
        return parsedMessage;

    }    
    
    
}
