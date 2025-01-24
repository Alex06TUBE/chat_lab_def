package com.alessio.chatdefinitivo;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerSocket
{
    // dichiarazione numero di porta
    int porta = 12345;

    // creazione della HashMap
    HashMap <String, Socket> connections = new HashMap<>();
    // costruttore ServerSocket

    public ServerSocket()
    {
        try(java.net.ServerSocket serverSocket = new java.net.ServerSocket(porta))
        {
            System.out.println("Server in ascolto sulla porta " + porta);

            // Attende una connessione da un client
            try(Socket clientSocket = serverSocket.accept())
            {
                System.out.println("Connessione accettata da " + clientSocket.getInetAddress());

                // Creazione degli stream di input e output

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Benvenuto/a e inviami il tuo nome: ");


                String inputLine;

                // legge i dati inviati dal client e li rimanda indietro (echo)

                while ((inputLine = in.readLine()) != null)
                {
                    System.out.println("Ricevuto dal client: " + inputLine);
                    out.println("Echo: " + inputLine);

                    if("exit".equalsIgnoreCase(inputLine))
                    {
                        break;
                    }
                }
            }

        } catch (IOException e)
        {
            System.err.println("Errore nel server: " + e.getMessage());
        }
    }

    // funzione invia messaggi
    public void sendMessage(String s, Socket sk)
    {
        try{
            sk.getOutputStream().write(s.getBytes());
        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    // funzione che gestisce i comandi
    public void handleCommand(String s, Socket sk)
    {
        String cmd = s.split(" ")[0].toUpperCase();

        switch (cmd)
        {
            // caso comando "DIRECT" per inviare un messaggio ad un determinato client
            case "DIRECT" :
                String dst = s.split(" ")[1].toUpperCase();
                Socket skt = connections.get(dst);
                String msg = s.split(" ", 2)[3];
                try
                {
                    PrintWriter out = new PrintWriter(sk.getOutputStream(), true);
                    out.println();
                }
                catch (IOException e)
                {
                    System.err.println("Errore nel server: " + e.getMessage());
                }

                break;

            // caso comando "BROADCAST" per inviare un messaggio a tutti i client
            case "BROADCAST" :
                break;

            // caso comando "LIST" per ricevere l'elenco dei attuali client connessi al server
            case "LIST" :
                break;

            // caso comando "QUIT" per chiudere la connessione e rimuovere il client dalla lista.
            case "QUIT" :
                break;

            // caso comando facoltativo "NICK" per cambiare dinamicamente il nome di un client già connesso."
            case "NICK" :
                break;
        }
    }

}
