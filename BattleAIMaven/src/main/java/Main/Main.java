package Main;

import Constants.MainConstants;
import Console.ConsoleFrame;
import Interface.MainFrame;
import Networking.Server.ServerDispatcher;
import java.awt.EventQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Main implements MainConstants {

    public static ConsoleFrame console;
    private static Boolean consoleReady = false;
    static MainFrame mainFrame;
    private static String DB_USER = "root";
    private static String DB_PASS = "";

    public static void main(String[] args) {
        System.out.println("---------------------------------------");
        System.out.println("            Started BattleAI           ");
        System.out.println("---------------------------------------\n");
        ConsoleFrame.showConsole = false;
        ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Deciding how to start application...");
        final boolean showConsole;
        final boolean isServer;
        if (args.length > 0) {
            switch (args[0]) {
                case MASTER_SERVER_CONSOLE: {
                    ConsoleFrame.showConsole = true;
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Start as server with visible console");
                    isServer = true;
                    showConsole = true;
                }
                break;
                case MASTER_SERVER_NO_CONSOLE: {
                    ConsoleFrame.showConsole = false;
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Start as server without visible console");
                    isServer = true;
                    showConsole = false;
                }
                break;
                case CLIENT_CONSOLE: {
                    ConsoleFrame.showConsole = true;
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Start as client with visible console");
                    isServer = false;
                    showConsole = true;
                }
                break;
                default: {
                    ConsoleFrame.showConsole = false;
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Start as client without visible console");
                    isServer = false;
                    showConsole = false;
                }
            }
            if (args.length > 1) {
                DB_USER = args[1];
            }
            if (args.length > 2) {
                DB_PASS = args[2];
            } else {
                DB_PASS = "";
            }
        } else {
            ConsoleFrame.showConsole = false;
            ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Start as client without visible console");
            showConsole = false;
            isServer = false;
        }

        //Starting main interface if is not server
        if (!isServer) {
            EventQueue.invokeLater(() -> {
                ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Initializing MainFrame");
                mainFrame = MainFrame.getInstance();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            });
        }

        //Starting console if wanted
        if (showConsole) {
            EventQueue.invokeLater(() -> {
                ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Initializing ConsoleFrame");
                synchronized (consoleReady) {
                    console = ConsoleFrame.getInstance();
                    console.setLocation(100, 100);
                    console.setVisible(true);
                    consoleReady = true;
                }
            });
            synchronized (consoleReady) {
                while (!consoleReady) {
                    try {
                        consoleReady.wait(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getSimpleName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        if (isServer) {
            if (DB_USER == null) {
                if (showConsole) {
                    DB_USER = JOptionPane.showInputDialog("Database Username:");
                    DB_PASS = JOptionPane.showInputDialog("Database Password:");
                } else {
                    Scanner scanner = new Scanner(System.in);
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Database Username:");
                    DB_USER = scanner.nextLine();
                    ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Database Password:");
                    DB_PASS = scanner.nextLine();
                }
            }
            ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Initializing Database with ('" + DB_USER + "' and '" + DB_PASS + "')");
            Database.DatabaseHandler.getInstance(DB_USER, DB_PASS);
            ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Database ready");
            ConsoleFrame.sendMessage(Main.class.getSimpleName(), "Initializing ServerDispatcher");
            ServerDispatcher.getInstance().start(Constants.MasterServerConstants.PORT);
            ConsoleFrame.sendMessage(Main.class.getSimpleName(), "ServerDispatcher ready");
        }
    }
}
