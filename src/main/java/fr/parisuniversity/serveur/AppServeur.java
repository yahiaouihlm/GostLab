package fr.parisuniversity.serveur;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class AppServeur {

    /*
     * @choisir un port de connexion
     * 
     */
    /*
     * Agil/Scrum :: sprint , PO :: relation avec le client
     * spring 2.x spring Boot spring Data spring MVC spring Security
     */
    public static List<Game> games = new Vector<>();

    public static void main(String[] args) {
        var port = new Random().nextInt(1000, 9999);
        try {
            if (args.length >= 1)
                port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println(" PORT MUST BE AN INTEGER  : " + port);
            System.exit(1);
        }

        // creation de la socket de serveur
        ServerSocket socketServeur = null;
        try {
            socketServeur = new ServerSocket(4242);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server Started Listen On Port " + port);

        while (true) {
            try {

                Socket socketClient = socketServeur.accept();
                Thread th = new Thread(new Lobby(socketClient));
                th.start();

            } catch (IOException ioe) {
                ioe.printStackTrace();

            }
        }

    }// La fin de La méthode main

    /*
     * si aucune game correspond à la numéro du la game rechercher
     * La methode renvoie null
     */
    public static Game getGame(List<Game> games, int gameNum) {
        for (Game game : games) {
            if (game.getnumGame() == gameNum)
                return game;
        }
        return null;
    }

    public static byte getNotStartedGames(List<Game> games) {
        byte gameNumbers = 0;
        for (Game game : games) {

            if (!game.getIsStarted())
                gameNumbers++;

        }
        return gameNumbers;
    }

}
