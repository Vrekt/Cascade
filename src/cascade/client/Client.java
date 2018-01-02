package cascade.client;

import cascade.Cascade;
import cascade.LogLevel;
import cascade.server.backend.ServerBackend;
import me.vrekt.chat.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Client {

    private final ServerBackend backend;

    private final Socket socket;
    private ObjectInputStream clientIn;
    private ObjectOutputStream clientOut;

    private boolean connected;
    private Session session;

    public Client(ServerBackend backend, Socket socket, int uniqueId) {
        this.backend = backend;
        this.socket = socket;

        if (socket == null) {
            Cascade.log("Couldn't connect client: " + uniqueId + " because of a null socket.", LogLevel.ERROR);
            return;
        }

        Cascade.log("Authenticating user: " + uniqueId, LogLevel.INFO);

        try {
            // create out streams.
            // TODO: Add handling for reversed stream creation.
            clientOut = new ObjectOutputStream(socket.getOutputStream());
            clientIn = new ObjectInputStream(socket.getInputStream());

            boolean authenticated = authenticate(uniqueId);
            if (!authenticated) {
                Cascade.log("Failed to authenticate user: " + uniqueId + ", bad login.", LogLevel.ERROR);
                return;
            }

            Cascade.log("Successfully authenticated user: " + uniqueId, LogLevel.INFO);
            connected = true;

            // handle updating this client.
            Thread thread = new Thread(() -> {
                while (connected) {
                    update();
                }
            });

            thread.start();

        } catch (IOException exception) {
            Cascade.log("Failed to authenticate user: " + uniqueId + ", an exception has occurred.", LogLevel.ERROR);
        }
    }

    /**
     * Update and receive information.
     */
    private void update() {
        try {
            Object object = clientIn.readObject();
            if (object instanceof Message) {
                // make sure the client actually sent a message.
                Message message = (Message) object;
                Message.MessageType type = message.getType();

                switch (type) {
                    case MESSAGE:
                        // get the message and broadcast it.
                        String msg = message.getMessage();
                        Cascade.log("[" + session.getUsername() + ":" + session.getUniqueId() + "]: " + msg, LogLevel.INFO);
                        backend.broadcast(session.getUsername() + ": " + msg, session.getUsername());
                        break;
                    case LOGIN:
                        break;
                    case KEEPALIVE:
                        session.setLastPingTime(System.currentTimeMillis());
                        break;
                }

            }
        } catch (IOException | ClassNotFoundException exception) {
            disconnect();
        }
    }

    /**
     * @param uniqueId the clients uniqueId.
     * @return true if the authentication was successful.
     */
    private boolean authenticate(int uniqueId) {
        try {
            Object object = clientIn.readObject();
            if (object instanceof Message) {
                // get the message and the type we sent.
                Message message = (Message) object;
                Message.MessageType type = message.getType();
                // make sure we actually sent a login first.
                if (type == Message.MessageType.LOGIN) {
                    String username = message.getMessage();
                    // TODO: Add a server config.
                    if (Objects.isNull(username) || username.length() > 16) {
                        // invalid username, return false.
                        return false;
                    }
                    // Authentication succeeded, set our session and return.
                    session = new Session(username, uniqueId);
                    return true;
                }
                return false;
            }
            return false;
        } catch (IOException | ClassNotFoundException exception) {
            return false;
        }

    }

    /**
     * Disconnect this client.
     */
    public void disconnect() {
        if (connected) {
            connected = false;
            // close all sockets.
            try {
                socket.close();
                clientIn.close();
                clientOut.close();
            } catch (IOException exception) {
                Cascade.log("Couldn't close socket/streams for client.", LogLevel.INFO);
            }
            backend.removeClient(this);
        }
    }

    /**
     * Send a message to this client.
     *
     * @param message the message
     */
    public void sendMessage(Message message) {
        try {
            clientOut.writeObject(message);
        } catch (IOException exception) {
            Cascade.log("Couldn't send message to client: " + session.getUniqueId() + " an exception occurred.", LogLevel.WARN);
        }
    }

    /**
     * @return if we are connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @return this clients session.
     */
    public Session getSession() {
        return session;
    }
}
