package fr.parisuniversity.LobbyTests;

import fr.parisuniversity.serveur.*;

import java.net.*;
import java.io.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JoinAndUnregGameTest {
    /********************************
     * Simulateur de Server
     ********************************/
    @BeforeAll
    static void initServer() throws UnknownHostException, IOException {
        Thread th = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(4141);
                server.accept();
                Thread.sleep(1000);
                server.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        th.start();
    }

    /************************************************************
     * l'initialisation de la listes des games apres avant test
     ***********************************************************/
    @AfterEach
    void initListGames() {
        AppServeur.games.clear();
        Game.idGame = 0;
    }

    @Test
    void JoinGameTestvalidateNameAndPortAndGameNum() {

        try (Socket socketClient = new Socket("localhost", 4141)) {
            Lobby lobby = new Lobby(socketClient);
            String anser = "REGNO***";

            Assertions.assertEquals(anser, lobby.joinGame("tes t", "1231", (byte) 99));
            Assertions.assertEquals(anser, lobby.joinGame("tes t", "1231", (byte) (-1)));
            AppServeur.games.add(new Game());
            Player player = new Player("tes1", "test2", socketClient);

            lobby.setPlayer(player);
            Assertions.assertEquals(anser, lobby.joinGame("tes t", "1231", (byte) 1));

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
