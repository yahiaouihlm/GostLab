package fr.parisuniversity.LobbyTests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.parisuniversity.serveur.*;

public class CreateGametest {

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
    void initListAndSocket() {
        AppServeur.games.clear();
        Game.idGame = 0;
    }

    @Test
    void createGameTestNullNameOrPort() {

        try (Socket socketClient = new Socket("localhost", 4141)) {
            Lobby lobby = new Lobby(socketClient);
            String anser = "REGNO***";
            Assertions.assertEquals(anser, lobby.createGame(null, null));
            Assertions.assertEquals(anser, lobby.createGame(null, "test"));
            Assertions.assertEquals(anser, lobby.createGame(" ", null));

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Test
    void createGameTestValidateNameAndPort() {

        try (Socket socketClient = new Socket("localhost", 4141)) {
            Lobby lobby = new Lobby(socketClient);
            String anser = "REGNO***";
            Assertions.assertEquals(anser, lobby.createGame("       ", "1231"));
            Assertions.assertEquals(anser, lobby.createGame("halim", "z321"));
            Assertions.assertEquals(anser, lobby.createGame("halim", ""));

            // GAME CREEÉ
            Assertions.assertEquals("REGOK 1***", lobby.createGame("halim", "2321"));
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Test
    void createGameTestExistingPlayer() {

        try (Socket socketClient = new Socket("localhost", 4141)) {
            Lobby lobby = new Lobby(socketClient);
            Assertions.assertEquals("REGOK 1***", lobby.createGame("TEST1", "1232"));
            // LE JOEUR EXISTE DEJA
            Assertions.assertEquals("REGNO***", lobby.createGame("TEST2", "8765"));
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
