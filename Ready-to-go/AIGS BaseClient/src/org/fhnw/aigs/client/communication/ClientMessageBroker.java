package org.fhnw.aigs.client.communication;

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
import javafx.application.Platform;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fhnw.aigs.client.GUI.LoadingWindow;
import org.fhnw.aigs.client.GUI.SetupWindow;
import org.fhnw.aigs.client.GUI.SettingsWindow;
import org.fhnw.aigs.client.gameHandling.ClientGame;
import org.fhnw.aigs.commons.GameMode;
import org.fhnw.aigs.commons.JoinType;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.commons.XMLHelper;
import org.fhnw.aigs.commons.communication.BadInputMessage;
import org.fhnw.aigs.commons.communication.ExceptionMessage;
import org.fhnw.aigs.commons.communication.ForceCloseMessage;
import org.fhnw.aigs.commons.communication.IdentificationResponseMessage;
import org.fhnw.aigs.commons.communication.JoinResponseMessage;
import org.fhnw.aigs.commons.communication.KeepAliveMessage;
import org.fhnw.aigs.commons.communication.Message;
import org.fhnw.aigs.commons.communication.NotifyMessage;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is responsible for parsing and handling all messages sent to the
 * client. In some way it is <b>the</b> most important class. It consists of two
 * parts:<br>
 * <ul><li>Message receving</li><li>Message handling</li></ul><br>
 * v1.0 Initial release<br>
 * v1.1 Functional changes<br>
 * v1.2 Added new messages and depending handling
 *
 * @author Matthias Stöckli (v1.0)
 * @version 1.2 (Raphael Stoeckli, 23.10.2014)
 */
public class ClientMessageBroker implements Runnable {

    /**
     * The socket which connects the client to the server
     */
    private static Socket socket;
    /**
     * The BufferedReader used to read the incoming messages
     */
    private static BufferedReader in;
    /**
     * Reference to the ClientGame
     */
    private static ClientGame clientGame;
    /**
     * The currently received message in a human readable form
     */
    private static String currentPrettyPrintedMessage = "";
    /**
     * Indicates whether the currently processed message is a message which is
     * not related to games, e.g. a JoinMessage etc.
     */
    private boolean nonGameMessageReceived = false;

    /**
     * Initializes the ClientMessageBroker and sets up a {@link Socket}.<br>
     * This class is responsible for parsing, interpreting and passing messages
     * to the game logic (client wise). This class is the counterpart of the
     * <b>ServerMessageBroker</b> on the server side.
     *
     * @param socket Socket which connects the server to the client.
     * @param game A reference to the game
     */
    public ClientMessageBroker(Socket socket, ClientGame game) throws IOException {
        ClientMessageBroker.clientGame = game;
        ClientMessageBroker.socket = socket;
        ClientMessageBroker.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    /**
     * Starts the message listening loop.
     */
    @Override
    public void run() {
        Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Client ready, waiting for incoming messages...");
        listenForMessages();
    }

    /**
     * This method constantly listens for new inputs coming from the server. The
     * messages are first parsed using {@link ClientMessageBroker#parseInput}.
     * Then the parsed messages will be handled. System messages will be handled
     * by <b>ServerMessageBroker.checkForNonGameMessages</b> on the server side.
     * All other messages are then passed to the
     * <b>ServerMessageBroker.processGameLogic</b> (also server side) method.
     */
    private void listenForMessages() {
        try {
            String inputString = "";

            while ((inputString = in.readLine()) != null) {
                Message parsedMessage = parseInput(inputString);

                if (parsedMessage instanceof KeepAliveMessage == false) {
                    printMessage(inputString);
                }
            //    if (parsedMessage instanceof GameStartMessage) {
            //       clientGame.getGameWindow().removeOverlay();
            //    }

                checkForNonGameMessages(parsedMessage);

                // Stop the processing if a non game message was
                // handled or if there is no game or the message is not valid.
                if (nonGameMessageReceived) {
                    nonGameMessageReceived = false;
                    continue;
                }

                clientGame.processGameLogic(parsedMessage);
            }
        } catch (IOException ex) {
            // Stops the game in the case of a lost connection.
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "Lost connection to the server. Most probably the server was shut down. The game was closed.");
            System.exit(0);
        } catch (Exception ex) {
            // Stops the game in the case of an exception.
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.SEVERE, "An exception on the client side occured.", ex);
            System.exit(0);
        }
    }

    /**
     * Send the message using the currently configured socket. Use this method
     * whenever possible. The player will be attached and can therefore be
     * identified by the server by using the {@link Message#getPlayer} method.
     *
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
     * Checks the incoming messages for the following system messages:<br><ul>
     * <li>{@link ForceCloseMessage}</li>
     * <li>{@link KeepAliveMessage}</li>
     * <li>{@link IdentificationResponseMessage}</li>
     * <li>{@link JoinResponseMessage}</li>
     * <li>{@link NotifyMessage}</li>
     * <li>{@link ExceptionMessage}</li>
     * <li>{@link BadInputMessage}</li>
     * </ul>
     *
     * @param parsedMessage The parsed message.
     */
    private void checkForNonGameMessages(Message parsedMessage) {
        // Alias for clarity
        Message m = parsedMessage;

        if (m instanceof ForceCloseMessage) {
            handleForceCloseMessage(parsedMessage);
        }
        if (m instanceof KeepAliveMessage) {
            handleKeepAliveMessage(parsedMessage);
        } else if (m instanceof IdentificationResponseMessage) {
            handleIdentificationResponseMessage(parsedMessage);
        } else if (m instanceof ExceptionMessage) {
            handleExceptionMessage(parsedMessage);
        } else if (m instanceof BadInputMessage) {
            handleBadInputMessage(parsedMessage);
        } else if (m instanceof NotifyMessage) {
            handleNotifyMessage(parsedMessage);
        } else if (m instanceof JoinResponseMessage) {
            handleJoinResponseMessage(parsedMessage);
        }
    }
    
    /**
     * Handles a notify message from the server. It will only show a message box
     *
     * @param parsedMessage The {@link NotifyMessage}.
     * @since v1.2
     */
    private void handleNotifyMessage(Message parsedMessage) {
        NotifyMessage notifyMessage = (NotifyMessage) parsedMessage;
        JOptionPane.showMessageDialog(null, notifyMessage.getMessage(), "Message from AIGS server", JOptionPane.INFORMATION_MESSAGE);
        nonGameMessageReceived = true;        // Was handled  
    } 
    
    /**
     * Handles a message from the server after a joining operation.<br>
     * A Message box will only appear if the state is false
     *
     * @param parsedMessage The {@link JoinResponseMessage}.
     * @since v1.2
     */
    private void handleJoinResponseMessage(Message parsedMessage) {
        JoinResponseMessage joinResponse = (JoinResponseMessage) parsedMessage;
        if (joinResponse.getJoinState() == false)
        {
            String caption;
            if (joinResponse.getJoinType() == JoinType.CreateNewGame)
            {
               caption = "Party could not be created"; 
            }
            else if (joinResponse.getJoinType() == JoinType.JoinParticularGame)
            {
               caption = "Party could not be joined"; 
            }
            else
            {
                caption = "No party could be joined or created";
            }
            JOptionPane.showMessageDialog(null, joinResponse.getMessage(), caption, JOptionPane.INFORMATION_MESSAGE);
            nonGameMessageReceived = true;        // Was handled
                Platform.runLater(new Runnable() {                              // Important! UI manipulation must be handled with runLater from another tread
                @Override
                public void run() {
                    clientGame.getGameWindow().setOverlay(new SetupWindow(clientGame));  // Show Setup window
                    clientGame.getGameWindow().getHeader().setStatusLabelText("");          // Reset State
                }
            });  
            Settings.getInstance().SetGameStop();                               
        }
        else //Operation successfull
        {
            if (joinResponse.getJoinType() == JoinType.Auto)
            {
                if (joinResponse.isGameCreated() == true) // Show overlay
                {
                    if (joinResponse.getGameMode() == GameMode.Multiplayer)
                    {
                        Platform.runLater(new Runnable() {                      // Important! UI manipulation must be handled with runLater
                        @Override
                        public void run() {
                              //clientGame.getGameWindow().removeOverlay();
                              clientGame.getGameWindow().setOverlay(new LoadingWindow());  // Show Waiting window
                              clientGame.getGameWindow().getHeader().setStatusLabelText("Waiting for other players");          // Reset State
                          }
                      });                    
                    }
                    else // Start Game in Single Player mode
                    {
                        Settings.getInstance().SetGameRunning();
                    }
                }
            }
            else
            {
                Settings.getInstance().SetGameRunning();
            }
        }
        
    }     

    /**
     * If the client needs to be closed this method informs the user about the
     * event and closes the window.
     *
     * @param parsedMessage The {@link ForceCloseMessage}.
     */
    private void handleForceCloseMessage(Message parsedMessage) {
        ForceCloseMessage forceCloseMessage = (ForceCloseMessage) parsedMessage;
        JOptionPane.showMessageDialog(null, forceCloseMessage.getReason(), "Game Over", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    /**
     * This method handles KeepAliveMessages by sending a KeepAliveMessage back
     * to the server. If the KeepAliveManager has been deactivated, this method
     * is not needed.
     *
     * @param parsedMessage the {@link KeepAliveMessage}.
     */
    private void handleKeepAliveMessage(Message parsedMessage) {
        if (parsedMessage instanceof KeepAliveMessage) {
            // Do not react to the KeepAliveMessage as long as there
            // is no player available - without a valid player and
            // his or her playername, the signal cannot be processed
            if (clientGame.getPlayer() != null) {
                KeepAliveMessage keepAliveMessage = (KeepAliveMessage) parsedMessage;
                KeepAliveMessage responseMessage = new KeepAliveMessage();
                responseMessage.setSentTime(keepAliveMessage.getSentTime());
                responseMessage.setAnswerTime(new Date());
                sendMessage(parsedMessage);
                nonGameMessageReceived = true;        // Was handled
            }
        }
    }

    /**
     * This method handles the server's response to a client login attempt. It
     * creates a new user file locally, if needed and shows a prompt if the user
     * name has never been typed in or if the user name and password do not
     * match.
     *
     * @param parsedMessage The {@link IdentificationResponseMessage}.
     */
    private void handleIdentificationResponseMessage(Message parsedMessage) {
        IdentificationResponseMessage identificationResponseMessage = (IdentificationResponseMessage) parsedMessage;

        // Check if the login was successful
        if (identificationResponseMessage.getLoginSuccessful() == false) {
            // Show the settings GUI
            Settings.getInstance().SetGameStop();
            SettingsWindow identificationGUI = new SettingsWindow();
            identificationGUI.setVisible(true);

            // Change the status label on the identification GUI
            SettingsWindow.notifyOfFailure(identificationResponseMessage.getReason());
        } 
        else {
            // add player based on the identification
            // this cannot be done earlier due to the fact that 
            // the server may have allocated another name to the user
            Player player = new Player(identificationResponseMessage.getLoginName(), identificationResponseMessage.getPlayerName(), false);
            clientGame.setPlayer(player);
            Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "Identification successful - new Player {0}", player.toString());
            clientGame.onGameReady();                                           // Notify client game about the established connection         
            nonGameMessageReceived = true;                                      // Was handled
        }
    }    

    /**
     * This method handles {@link ExceptionMessage}s. They are sent to the
     * client when an exception occured. The client is then informed about this
     * event. Afterwards, the client will close.
     *
     * @param parsedMessage The {@link ExceptionMessage}.
     */
    private void handleExceptionMessage(Message parsedMessage) {
        JOptionPane.showMessageDialog(null,
                "There was an error on the server side of your game. The following stack trace should help you:\r\n"
                + currentPrettyPrintedMessage,
                "Game Over", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    /**
     * This method handles {@link BadInputMessage}s. They are sent to the client
     * when the client sends a bad input (e.g. wrong formatted XML) The client
     * is then informed about this event. The user can decide whether the client
     * shall be shut down or not.
     *
     * @param parsedMessage The {@link BadInputMessage}.
     */
    private void handleBadInputMessage(Message parsedMessage) {
        BadInputMessage badInputMessage = (BadInputMessage) parsedMessage;
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

    /**
     * Adds incoming message to the log file.
     *
     * @param inputString The message string.
     */
    private void printMessage(String inputString) {
        currentPrettyPrintedMessage = XMLHelper.prettyPrintXml(inputString);
        Logger.getLogger(ClientMessageBroker.class.getName()).log(Level.INFO, "<= \n {0}", currentPrettyPrintedMessage);
    }

    /**
     * This method is connected to
     * {@link ClientMessageBroker#handleIdentificationResponseMessage}. If the
     * user connects to the server for the first time, a file is being created
     * which stores the user's name and identification code.
     * @deprecated Method not used anymore. Please remove from code
     */
    private void writeUserFile(String userName, String identificationCode, String serverAddress, String serverPort) {
        File userFile = new File("aigs.user");

        // Check whether the file already exists.
        if (userFile.exists() == false) {
            boolean success = false;
            try {
                success = userFile.createNewFile();
                try (PrintWriter pw = new PrintWriter(userFile)) {
                    // Strip all numbers. This is necessary when the server is
                    // running on localhost or the IsMultiLoginAllowed option
                    // is set on true on the server side.
                    Pattern p = Pattern.compile("(\\w*\\.\\w*)(\\d)");
                    Matcher m = p.matcher(userName);
                    if (m.find()) {
                        userName = m.group(1);
                    }

                    // Write the file.
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

            if (success == false) {
                JOptionPane.showMessageDialog(null, "Could not create user file. You will have to log in the next time again.",
                        "Could not create file", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
