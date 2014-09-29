package org.fhnw.aigs.server.gameHandling;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fhnw.aigs.commons.Game;
import org.fhnw.aigs.commons.Player;
import org.fhnw.aigs.server.communication.ServerCommunication;

/**
 * This class is responsible for console input when the server is run in
 * console mode.
 * @author Matthias Stöckli (v1.0)
 * @version 1.1 (Raphael Stoeckli, 12.08.2014)
 */
public class AIGSConsoleHandler {
    
    /**
     * Scanner for user input.
     */
    Scanner scanner;
    
    /**
     * Contains the current user input as a string.
     */
    String currentInput;
    
    /**
     * Create a new instance of the AIGSConsoleHandler and initialize the scanner.
     * Use a new line as delimiter.
     */
    public AIGSConsoleHandler(){
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
    }
    
    /**
     * Waits for user input (console commands) and processes it.
     */
    public void runInputLoop(){
        
        while(true){
            currentInput = scanner.next().trim();
            
            // If the user types in "exit", the server will
            // shut down, "help" will show which console commands can be used,
            // "recompile" will terminate all games and recompile/reload the games,
            // "list" prints out a list of all the games and "terminate"
            // ends a game.
            // "start" and "stop" are starting or stopping the service.
            // "games" shows a list of installed games on the server
            if(currentInput.startsWith("exit")){
                shutdownServer();
            }else if(currentInput.startsWith("start")){
                startServer();
            }
            else if(currentInput.startsWith("stop")){
                stopServer();
            }
            else if(currentInput.startsWith("games")){
                listAvailableGames();
            }            
            else if(currentInput.startsWith("logs")){
                showLogs();
            }else if(currentInput.startsWith("help")){
                showHelp();
            }else if(currentInput.startsWith("recompile")){
                recompileClasses();
            }else if(currentInput.startsWith("list")){
                listActiveGames();
            }else if(currentInput.startsWith("terminate")){
                terminateGame(currentInput);
            }
            
        }
    }

    /**
     * Method to start the server
     * @since v1.1
     */
    private void startServer()
    {
        System.out.println("Starting server...");
        StartServerAction action = new StartServerAction();
        if (ServerCommunication.getInstance().getRunState() == true)
        {
            System.out.println("Server allready running. user 'stop' command first to restart");
            return;
        }
        else
        {
        action.startServer();
        System.out.println("Server started");
        }
    }
    
    /**
     * Method to stop the service (no shutdown)
     * @since v1.1
     */
    private void stopServer()
    {
        System.out.println("Stopping server...");
        StopServerAction action = new StopServerAction();
        action.stopServer();
        System.out.println("Server stopped");
    }
    
    /**
     * Show list of all installed games
     * @since v1.1
     */
    private void listAvailableGames()
    {
        ArrayList<String> games = GameLoader.getInstalledGames();
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "List installed games...");
        System.out.println("Installed games:\r\n----------------------");
        for(String game : games)
        {
        System.out.println(game);    
        }        
    }
    
    /**
     * Shuts down the server.
     */
    private void shutdownServer() {
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "Shut down server...");
        System.exit(0);
    }

    /**
     * Opens the logs folder.
     */
    private void showLogs() {
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "Show logs folder...");
        new ShowLogsAction().showLogs();
    }

    /**
     * Show all possible console commands.
     */
    private void showHelp() {
        System.out.println("You can type the following commands:\r\n");
        System.out.println("start: Start the service of the server.\r\n");
        System.out.println("stop: Stop the service of the server.\r\n");
        System.out.println("exit: Shut down server.\r\n");
        System.out.println("logs: Open the logs folder.\r\n");
        System.out.println("recompile: Stops all games and recompiles\reloads the game chars. \r\n");
        System.out.println("list: Show a list of all waiting or running games.\r\n");
        System.out.println("games: Show a list of all installed games on the server.\r\n");
        System.out.println("Terminate [gameID]: Stops the game with the specified"
                + "game ID. Use 'all' to termiante  all games. \r\n");
    }

    /**
     * Terminate all games and reload classes.
     */
    private void recompileClasses() {
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "Recompile and reload classes...");
        new RecompileClassesAction().reloadClasses();
    }

    /**
     * List all waiting and running games.
     */
    private void listActiveGames() {
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "List waiting and active games...");
        System.out.println("Waiting games:\r\n----------------------");
        for(Game game : GameManager.waitingGames){
            System.out.println(game.toString());
            System.out.println("Participants:");
            for(Player player : game.getPlayers()){
                System.out.println(player.toString());
            }
        System.out.println("\r\n-----");
        }
        
        System.out.println("Running games:\r\n----------------------");
        for(Game game : GameManager.runningGames){
            System.out.println(game.toString());
            System.out.println("Participants:");
            for(Player player : game.getPlayers()){
                System.out.println(player.toString());
            }
        System.out.println("\r\n-----");
        
        }
    }

    /**
     * Terminates the specified game using it's ID.
     * @param terminateString The input string.
     */
    private void terminateGame(String terminateString) {
            Pattern p = Pattern.compile("(terminate\\x20)(.+|\\w+|\\W+)?");
            Matcher m = p.matcher(terminateString);
            if (m.find()) {
                String toBeTerminated = m.group(2);
                if(toBeTerminated.equals("all")){
                    terminateAllGames();
                }
                else{
                    terminateSpecifiedGame(toBeTerminated);
                }
                }
                
    }

    /**
     * Terminates all waiting and running games.
     */
    private void terminateAllGames() {
        Logger.getLogger(AIGSConsoleHandler.class.getName()).log(Level.INFO, "Terminate all games...");
        for(Game game : GameManager.waitingGames){
            GameManager.terminateGame(game, "Termination requested by administrator.");
        }
        for(Game game : GameManager.runningGames){
            GameManager.terminateGame(game, "Termination requested by administrator.");
        }
    }

    /**
     * Terminates a single game.
     * @param toBeTerminated The ID of the game which will be terminated.
     */
    private void terminateSpecifiedGame(String toBeTerminated) {
        try{
            int id = Integer.parseInt(toBeTerminated);
            
            for(int i = 0; i < GameManager.waitingGames.size(); i++){
                Game game = GameManager.waitingGames.get(i);
                if(game.getId() == id){
                    GameManager.terminateGame(game, "Termination requested by administrator.");
                }
            }
            
            for (int i = 0; i < GameManager.runningGames.size(); i++) {
                Game game = GameManager.runningGames.get(i);
                if (game.getId() == id) {
                    GameManager.terminateGame(game, "Termination requested by administrator.");
                }
            }
            
        }catch(NumberFormatException ex){
            System.out.println("Invalid input. Either type the id of a game"
                    + "or type 'all' to terminate all games. In order to get"
                    + "a list of all games, type 'list'");
        }
        catch (Exception ex) // All other exceptions
        {
            System.out.println("An unknown error occurred: " + ex.getMessage());
        }
    }
    
}
