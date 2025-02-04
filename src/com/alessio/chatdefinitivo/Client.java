package com.alessio.chatdefinitivo;

// Client.java
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "192.168.153.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connesso al server. Inserisci il tuo nickname:");
            String nickname;
            while ((nickname = console.readLine()) != null) {
                out.println(nickname);
                in.readLine();
                String response = in.readLine();
                if (response.equals("Benvenuto, " + nickname + "!")) {
                    System.out.println(response);
                    break;
                } else {
                    System.out.println(response);
                }
            }

            Thread listener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Connessione chiusa dal server.");
                }
            });
            listener.start();

            String command;
            while ((command = console.readLine()) != null) {
                out.println(command);
                if (command.equals("QUIT")) {
                    System.out.println("Disconnesso dal server.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
