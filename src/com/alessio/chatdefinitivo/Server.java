package com.alessio.chatdefinitivo;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{
    private static final int PORT = 12345;
    // !!!! ----> Questa è un'HashMap che ha come chiave il nickname e di valore ClientHandler che è un Runnable <---- !!!!
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Server avviato su localhost, porta " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nickname;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.out = out;
                out.println("Inserisci il tuo nickname:");

                while (true) {
                    nickname = in.readLine();
                    if (nickname == null || nickname.isBlank() || clients.containsKey(nickname)) {
                        out.println("Nickname non valido o già in uso. Riprova:");
                    } else {
                        synchronized (clients) {
                            clients.put(nickname, this);
                            break;
                        }
                    }
                }
                out.println("Benvenuto, " + nickname + "!");
                broadcast(nickname + " si è connesso.", null);

                String message;
                while ((message = in.readLine()) != null)
                {
                    if (message.startsWith("DIRECT "))
                    {
                        handleDirectMessage(message);
                    }
                    else if (message.startsWith("BROADCAST "))
                    {
                        handleBroadcastMessage(message);
                    }
                    else if (message.equals("LIST"))
                    {
                        handleListRequest();
                    }
                    else if (message.equals("QUIT"))
                    {
                        break;
                    }
                    else if (message.startsWith("NICK"))
                    {
                        handleNickRequest(message);
                    }
                    else
                    {
                        out.println("Comando non riconosciuto.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }

        private void handleDirectMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                out.println("Formato comando non valido. Usa: DIRECT <destinatario> <messaggio>");
                return;
            }
            String recipient = parts[1];
            String text = parts[2];
            ClientHandler recipientHandler = clients.get(recipient);
            if (recipientHandler != null) {
                recipientHandler.out.println("[Privato da " + nickname + "]: " + text);
                out.println("Messaggio inviato a " + recipient);
            } else {
                out.println("Utente " + recipient + " non trovato.");
            }
        }

        private void handleBroadcastMessage(String message) {
            String text = message.substring(10);
            broadcast("[Broadcast da " + nickname + "]: " + text, this);
        }

        private void handleListRequest() {
            out.println("Utenti connessi: " + String.join(", ", clients.keySet()));
        }

        private void broadcast(String message, ClientHandler exclude) {
            for (ClientHandler client : clients.values()) {
                if (client != exclude) {
                    client.out.println(message);
                }
            }
        }

        private void handleNickRequest(String message)
        {
            String newNickname = message.split(" ")[1];
            out.println("Nickname cambiato in: " + newNickname);
            synchronized (clients) {
                clients.put(newNickname, this);
                clients.remove(this.nickname);
            }
            this.nickname = newNickname;
        }

        private void disconnect() {
            if (nickname != null) {
                clients.remove(nickname);
                broadcast(nickname + " si è disconnesso.", null);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

