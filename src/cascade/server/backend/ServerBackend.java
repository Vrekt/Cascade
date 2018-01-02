package cascade.server.backend;

import cascade.Cascade;
import cascade.LogLevel;
import cascade.client.Client;
import cascade.server.task.ServerTaskExecutor;
import me.vrekt.chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerBackend {

    private final ServerTaskExecutor EXECUTOR = new ServerTaskExecutor(this);
    private final ServerSocket server;

    private final List<Client> CLIENTS = new ArrayList<>();
    private final List<Client> QUEUE = new ArrayList<>();

    public ServerBackend(ServerSocket socket) {
        this.server = socket;
    }

    /**
     * Start the executor.
     */
    public void start() {
        EXECUTOR.start();
    }

    /**
     * Accept new clients.
     */
    public void acceptClient() {
        try {
            // create the new client if we accepted.
            Socket socket = server.accept();
            Client client = new Client(this, socket, CLIENTS.size() + 1);
            if (client.isConnected()) {
                // add the client.
                CLIENTS.add(client);
            }
        } catch (IOException exception) {
            Cascade.log("Couldn't accept new client.", LogLevel.WARN);
        }
    }

    /**
     * Validate all connected clients are still connected.
     */
    public void validate() {
        // TODO: Server config option for timeout.
        long now = System.currentTimeMillis();
        List<Client> expired = new ArrayList<>();

        for (Client client : CLIENTS) {
            if (client == null) {
                continue;
            }
            long time = now - client.getSession().getLastPingTime();
            if (time >= 1500) {
                // client timed out, log and remove.
                Cascade.log("Disconnected client: " + client.getSession().getUniqueId() + " for timing out.", LogLevel.INFO);
                expired.add(client);
            }
        }

        // now remove queued clients.
        QUEUE.forEach(CLIENTS::remove);
        QUEUE.clear();

        // remove and clear.
        expired.forEach(CLIENTS::remove);
        expired.clear();
    }

    /**
     * Send a message to all connected users.
     *
     * @param message the message
     */
    public void broadcast(String message, String username) {
        Message msg = new Message(Message.MessageType.MESSAGE, message);
        CLIENTS.stream().filter(client -> !client.getSession().getUsername().equals(username)).forEach(client -> client.sendMessage(msg));
    }

    /**
     * Remove a client.
     *
     * @param client the client
     */
    public void removeClient(Client client) {
        QUEUE.add(client);
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        // disconnect and clear.
        CLIENTS.forEach(Client::disconnect);
        CLIENTS.clear();

        // close the socket.
        try {
            server.close();
        } catch (IOException exception) {
            Cascade.log("Couldn't close server socket.", LogLevel.WARN);
        }
    }

}
