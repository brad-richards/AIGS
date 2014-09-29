package org.fhnw.aigs.swingClient.communication;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fhnw.aigs.swingClient.GUI.SettingsWindow;
import org.fhnw.aigs.swingClient.gameHandling.ClientGame;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.XMLHelper;
import org.fhnw.aigs.commons.communication.BadInputMessage;
import org.fhnw.aigs.commons.communication.ExceptionMessage;
import org.fhnw.aigs.commons.communication.ForceCloseMessage;
import org.fhnw.aigs.commons.communication.IdentificationResponseMessage;
import org.fhnw.aigs.commons.communication.KeepAliveMessage;
import org.fhnw.aigs.commons.communication.Message;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is responsible for parsing and handling all messages sent to the client.
 * In some way it is <b>the</b> most important class. It consists of two parts:<br>
 * Message receiand message handling.
 * @author Matthias Stöckli
 */

public class ClientMessageBroker implements Runnable {

    /** The socket which connects the client to the server */
    private static Socket socket;
    
    /** The BufferedReader  used to read the incoming messages*/
    private static BufferedReader in;
    
    /** Reference to the ClientGame */
    private static ClientGame clientGame;
    
    /** The currently received message in a human readable form */
    private static String currentPrettyPrintedMessage = "";

    /** Indicates whether the currently processed message is a message 
     which is not related to games, e.g. a JoinMessage etc.
     */
    private boolean nonGameMessageReceived = false;

    /**
     * Initializes the ClientMessageBroker and sets up a {@link Socket}.<br>
     * This class is responsible for parsing, interpreting and passing messages
     * to the game logic (client wise).
     * This class is the counterpart of the <b>ServerMessageBroker</b> on the
     * server side. 
     * @param socket Socket which connects the server to the client.
     * @param game A reference to the game
     */
    public ClientMessageBroker(Socket socket, ClientGame game) throws IOException {
        ClientMessageBroker.clientGame = game;
        ClientMessageBroker.socket = socket;
        ClientMessageBroker.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    @Override
    public void run() {
        Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Client ready, waiting for incoming messages...");
        listenForMessages();
    }

    private void listenForMessages() {
        try {
            String inputString = "";
            
            while ((inputString = in.readLine()) != null) {
                Message parsedMessage = parseInput(inputString);
                
                if(parsedMessage instanceof KeepAliveMessage == false){
                        printMessage(inputString);                    
                }

                checkForNonGameMessages(parsedMessage);
                
                // Don't process message any further, if a non game message was handled 
                if (nonGameMessageReceived) {
                    nonGameMessageReceived = false;
                    continue;
                }
                
                clientGame.processGameLogic(parsedMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "Lost connection to the server. Most probably the server was shut down. The game was closed.");
            System.exit(0);
        }catch(Exception ex){
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "An exception on the client side occured.", ex);
            System.exit(0);
        }
    }

    /**
     * Send the message using the currently configured socket. Use this method
     * whenever possible. The player will be attached and can therefore be identified
     * by the server by using the {@link Message#getPlayer} method. 
     * @param message The message to be sent.
     */
    public static void sendMessage(Message message) {
        message.send(socket, clientGame.getPlayer());
    }
    
    /**
     * This method parses the incoming messages.<br>
     * It first tries to create a DOMDocument in order to check whether the
     * string is valid xml and to determine the message type. If the message
     * cannot be parsed, the client will receive a {@link BadInputMessage}. If
     * the parsing is successful, the respective message class is loaded via the
     * system ClassLoader.<br>
     * In a last step, the {@link Message#parse} method will take care of the
     * parsing itself.
     *
     * @param inputString The message as received from the clients.
     * @return The parsed message.
     */
    private Message parseInput(String inputString) {
        String messageClassPath = null;
        Message parsedMessage = null;
        Class messageClass = null;

        // Read MessageType for further processing
        StringReader reader = new StringReader(inputString);                    // Used to create an input source and for parsing
        InputSource inputSource = new InputSource(reader);                      // Used for DOM Parsing of message (find out the type)
        // Used to find out the type of message
        Document DOMDocument;
        try {
            DOMDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
            // Read the attribute "classPath" of the sent XML. It represents the qualified name of the class
            // e.g. "org.fhnw.aigs.commons.TicTacToe.FieldClickMessage" which points to the class "FieldClickMessage"
            messageClassPath = DOMDocument.getDocumentElement().getAttribute("FullyQualifiedClassName");

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "Could not parse input into xml format", ex);
        }
        catch (Exception ex) // All other exceptions
        {
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "An unknown Error occurred.", ex);
        }

        reader = new StringReader(inputString);                                 // Recreate the reader because the string was read with the last operation


        // Try to load and parse the Message Class dynamically by the name provided by the attribute "FullyQualifiedName"
        // which is provided by every Message.
        try {
            messageClass = Class.forName(messageClassPath);
            parsedMessage = Message.parse(reader, messageClass);     //
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "Could not find a matching class. Check the package name, @XmlElement annotations and the jars.", ex);
        }
        catch (Exception ex) // All other Exceptions
        {
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "An unknown error occurred.", ex);
        }

        return parsedMessage;
    }

    /**
     * Checks for all non game messages like <b>GameStartMesage</b> or <b>GameEndMessage</b>
     * and handle them.
     * @param parsedMessage 
     */
    private void checkForNonGameMessages(Message parsedMessage) {
        // Alias for clarity
        Message m = parsedMessage;
    
        if(m instanceof ForceCloseMessage)
            handleForceCloseMessage(parsedMessage);
        if(m instanceof KeepAliveMessage)
            handleKeepAliveMessage(parsedMessage);
        else if(m instanceof IdentificationResponseMessage)
            handleIdentificationResponseMessage(parsedMessage);
        else if(m instanceof ExceptionMessage)
            handleExceptionMessage(parsedMessage);
        else if(m instanceof BadInputMessage)
            handleBadInputMessage(parsedMessage);
    }
    
    private void handleForceCloseMessage(Message parsedMessage) {
        ForceCloseMessage forceCloseMessage = (ForceCloseMessage) parsedMessage;
        JOptionPane.showMessageDialog(null, forceCloseMessage.getReason(), "Game Over", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }    
    
    private void handleKeepAliveMessage(Message parsedMessage) {
        if (parsedMessage instanceof KeepAliveMessage) {
            // Do not react to the KeepAliveMessage as long as there
            // is no player available - without a valid player and
            // his or her playername, the signal cannot be processed
            if(clientGame.getPlayer() != null){
                KeepAliveMessage keepAliveMessage = (KeepAliveMessage) parsedMessage;
                KeepAliveMessage responseMessage = new KeepAliveMessage();
                responseMessage.setSentTime(keepAliveMessage.getSentTime());
                responseMessage.setAnswerTime(new Date());
                sendMessage(parsedMessage);
                nonGameMessageReceived = true;        // Was handled
            }
        }
    }
    
/*    
    private void handleIdentificationResponseMessage(Message parsedMessage) {
            IdentificationResponseMessage identificationResponseMessage = (IdentificationResponseMessage) parsedMessage;
            
            if(identificationResponseMessage.getLoginSuccessful() == false){                
                if(ClientCommunication.hasReadFromFile){
                    ClientCommunication.hasReadFromFile = false;
                    File userFile = new File("aigs.user");
                    boolean deleteSuccess = false;
                    if(userFile.exists()){
                        deleteSuccess = userFile.delete();      // Delete the erroneous file
                    }
                    if(deleteSuccess == false){
                        JOptionPane.showMessageDialog(null, "Could not delete corrupt " +
                                "\"aigs.user\" file. Please delete or correct it manually.", "Could not delete",JOptionPane.WARNING_MESSAGE);
                        File userFolder = new File(new java.io.File("").getAbsolutePath());
                        // Show folder
                        try {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(userFolder);
                            } else {
                                JOptionPane.showMessageDialog(null, "Not supported on this OS", "Not supported", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (IOException ex2) {
                            JOptionPane.showMessageDialog(null, ex2.getMessage(), "Could not read path.", JOptionPane.ERROR_MESSAGE);
                            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "Could not read path", ex2);
                        }

                        Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Could not delete aigs.user file");
                        
                    }
                    SettingsWindow identificationGUI = new SettingsWindow(clientGame);
                    identificationGUI.setVisible(true);
                }                
                SettingsWindow.notifyOfFailure(identificationResponseMessage.getReason());                
            } else {
                if(ClientCommunication.hasReadFromFile == false){
                    SettingsWindow.notifyOfSuccess();                
                }
                // add player based on the identification
                // this cannot be done earlier due to the fact that 
                // the server may have allocated another name to the user
                Player player = new Player(identificationResponseMessage.getUserName(), false);
                clientGame.setPlayer(player);
                Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Identification successful - new Player {0}", player.toString());
                writeUserFile(identificationResponseMessage.getUserName(), identificationResponseMessage.getIdentificationCode());
                clientGame.onGameReady();                   // Notify client game about the established connection         
                nonGameMessageReceived = true;        // Was handled
            }
    }
*/
    
    private void handleIdentificationResponseMessage(Message parsedMessage) {
            IdentificationResponseMessage identificationResponseMessage = (IdentificationResponseMessage) parsedMessage;
            if(identificationResponseMessage.getLoginSuccessful() == false){                
                    // Show the settings GUI
                    SettingsWindow identificationGUI = new SettingsWindow();
                    identificationGUI.setVisible(true);
                    // Change the status label on the identification GUI                
                    SettingsWindow.notifyOfFailure(identificationResponseMessage.getReason());                
            } else {
                // add player based on the identification
                // this cannot be done earlier due to the fact that 
                // the server may have allocated another name to the user
                Player player = new Player(identificationResponseMessage.getUserName(), false);
                clientGame.setPlayer(player);
                Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Identification successful - new Player {0}", player.toString());
                clientGame.onGameReady();                   // Notify client game about the established connection         
                nonGameMessageReceived = true;        // Was handled
            }
    }    


    private void handleExceptionMessage(Message parsedMessage) {
            JOptionPane.showMessageDialog(null,
                    "There was an error on the server side of your game. The following stack trace should help you:\r\n"
                    + currentPrettyPrintedMessage,
                    "Game Over", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
    }
    
    private void handleBadInputMessage(Message parsedMessage) {
        BadInputMessage badInputMessage = (BadInputMessage)parsedMessage;
                Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Server reported bad input: " + badInputMessage.getInput(), currentPrettyPrintedMessage);
                int result = JOptionPane.showConfirmDialog(null,
                        "This client sent a string to the server which it could "
                        + "not process.\n"
                        + "The string has been stored in the logs.\n"
                        + "This may cause unpredictable behaviour.\n"
                        + "Do you want to proceed?",
                        "Close the application?",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
    }    
    
        private void printMessage(String inputString) {
        currentPrettyPrintedMessage = XMLHelper.prettyPrintXml(inputString);
        Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "<= \n {0}", currentPrettyPrintedMessage);        
        }

        /**
         * 
         * @param userName
         * @param identificationCode 
         * @deprecated Method not used. Please remove from code
         */
    private void writeUserFile(String userName, String identificationCode){
        File userFile = new File("aigs.user");
        if(userFile.exists() == false){
            boolean success = false;
            try {
                success = userFile.createNewFile();
                try (PrintWriter pw = new PrintWriter(userFile)) {
                    // Strip all numbers
                    Pattern p = Pattern.compile("(\\w*\\.\\w*)(\\d)");
                    Matcher m = p.matcher(userName);
                    if (m.find()) {
                        userName = m.group(1);
                    }
                            
                    pw.println(userName);
                    pw.println(identificationCode);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Could not create user file. You will have to log in the next time again.",
                        "Could not create file", JOptionPane.WARNING_MESSAGE);
            }
            catch (Exception ex) // All other exceptions
            {
                Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "An unknown error occurred while crating user file.",
                        "Could not create file", JOptionPane.WARNING_MESSAGE);                
            }
            
            if(success == false){
                JOptionPane.showMessageDialog(null, "Could not create user file. You will have to log in the next time again.",
                        "Could not create file", JOptionPane.WARNING_MESSAGE);
            }
            
        }
    }
    }
