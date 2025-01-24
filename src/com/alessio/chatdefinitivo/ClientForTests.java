package com.alessio.chatdefinitivo;

// Client.java

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientForTests {
    private static final String SERVER_ADDRESS = "127.0.0.1";
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
