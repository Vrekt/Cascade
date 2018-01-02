package me.vrekt.client;

import me.vrekt.chat.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username, ip;
    private int port;

    private Socket socket;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private boolean connected, inputReady;

    private long lastPingTime = System.currentTimeMillis();

    public Client(String username, String ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;

        connect();

    }

    private void connect() {
        System.out.println("Attempting to connect to: " + ip + ":" + port);
        try {
            socket = new Socket(ip, port);

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(new Message(Message.MessageType.LOGIN, username));
            output.flush();
            connected = true;

            System.out.println("Connected!");

            Thread pingThread = new Thread(() -> {
                while (connected) {
                    long time = System.currentTimeMillis() - lastPingTime;
                    if (time >= 500) {
                        lastPingTime = System.currentTimeMillis();
                        try {
                            output.writeObject(new Message(Message.MessageType.KEEPALIVE));
                        } catch (IOException exception) {
                            disconnect();
                        }
                    }
                }
            });

            Thread serverUpdateThread = new Thread(() -> {
                while (connected) {
                    try {
                        Message msg = (Message) input.readObject();
                        String message = msg.getMessage();
                        System.out.println(message);
                        inputReady = false;
                    } catch (IOException | ClassNotFoundException exception) {
                        disconnect();
                    }
                }
            });

            pingThread.start();
            serverUpdateThread.start();

            // get input

            Scanner scanner = new Scanner(System.in);
            while (connected) {
                if (inputReady) {
                    System.out.print("> ");
                    String message = scanner.nextLine();
                    if (message != null) {
                        try {
                            output.writeObject(new Message(Message.MessageType.MESSAGE, message));
                        } catch (IOException exception) {
                            disconnect();
                        }
                    }
                }
                inputReady = true;
            }


        } catch (IOException exception) {
            System.out.println("Failed to connect.");
            System.exit(-1);
        }
    }

    private void disconnect() {
        if (connected) {
            connected = false;
            try {
                if (socket != null) {
                    socket.close();
                }

                if (output != null) {
                    output.close();
                }

                if (input != null) {
                    input.close();
                }
            } catch (IOException exception) {
                // Nothing to do.
            }
        }
    }

}
