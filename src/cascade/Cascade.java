package cascade;

import cascade.server.CascadeServer;

public class Cascade {

    private static CascadeServer server;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please specify a port.");
            System.exit(-1);
        }

        String portArgument = args[0];
        int port = 0;
        try {
            port = Integer.parseInt(portArgument);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid port number.");
            System.exit(-1);
        }

        // open server.
        server = new CascadeServer(port);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
    }

    /**
     * Log a message to console.
     *
     * @param log   the message
     * @param level the level severity.
     */
    public static void log(String log, LogLevel level) {
        System.out.println("[SERVER] [" + level.toString() + "] " + log);
    }

    /**
     * @return the server
     */
    public static CascadeServer getServer() {
        return server;
    }
}
