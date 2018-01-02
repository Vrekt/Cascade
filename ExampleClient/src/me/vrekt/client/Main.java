package me.vrekt.client;

public class Main {

    public static void main(String[] args) {
        if (args.length <= 2) {
            System.out.println("Please run cascade with the arguments: <username> <ip> <port>");
            System.exit(-1);
        }

        String username = args[0];
        if (username == null) {
            System.out.println("Invalid username!");
            System.exit(-1);
        }
        String ip = args[1];
        int port = 0;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid port number!");
            System.exit(-1);
        }

        new Client(username, ip, port);

    }

}
