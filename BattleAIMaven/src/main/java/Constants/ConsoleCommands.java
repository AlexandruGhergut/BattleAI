package Constants;

/**
 *
 * @author Dragos-Alexandru
 */
public enum ConsoleCommands {
    CHECK_CONNECTION("/checkConn", "Returns all the active connections on the server"),
    HELP("/help", "Shows all posible commands on console"),
    EXIT("/exit", "Closes server");

    String commentary;
    String command;

    private ConsoleCommands(String command, String commentary) {
        this.command = command;
        this.commentary = commentary;
    }

    public String getValue() {
        return command;
    }

    public String getCommentary() {
        return commentary;
    }
}
