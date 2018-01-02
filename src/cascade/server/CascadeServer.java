package cascade.server;

import cascade.Cascade;
import cascade.LogLevel;
import cascade.server.backend.ServerBackend;

import java.io.IOException;
import java.net.ServerSocket;

public class CascadeServer {

    private boolean running;
    private ServerBackend backend;

    public CascadeServer(int port) {
        Cascade.log("Binding to port " + port + ".", LogLevel.INFO);
        try {
            // create new ServerSocket.
            ServerSocket server = new ServerSocket(port);
            backend = new ServerBackend(server);
        } catch (IOException exception) {
            Cascade.log("Could not bind to port: " + port + " perhaps a server is already running on that port?", LogLevel.CRITICAL);
            System.exit(-1);
        }
    }

    /**
     * Start the server.
     */
    public void start() {
        Cascade.log("Starting server...", LogLevel.INFO);
        running = true;

        backend.start();

    }

    /**
     * Stop the server.
     */
    public void stop() {
        Cascade.log("Stopping server...", LogLevel.INFO);
        running = false;

        backend.shutdown();
    }

    /**
     * @return if the server is running.
     */
    public boolean isRunning() {
        return running;
    }
}
