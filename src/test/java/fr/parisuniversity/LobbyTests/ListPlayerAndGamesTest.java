package fr.parisuniversity.LobbyTests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;

import fr.parisuniversity.serveur.AppServeur;
import fr.parisuniversity.serveur.Game;
import fr.parisuniversity.serveur.Lobby;
import fr.parisuniversity.serveur.Player;

public class ListPlayerAndGamesTest {

  /**********************************
   * Simulation Serveur
   ***************************************/
  Socket socketClient;
  Player player1 = new Player("test1", "1234", socketClient);
  Player player2 = new Player("test2", "1234", socketClient);
  Player player3 = new Player("test3", "1234", socketClient);

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

  /**********************************
   * initialisation de la game apres chaque test
   ***************************************/
  @AfterEach
  void initListGames() {
    AppServeur.games.clear();
    Game.idGame = 0;
  }

  /*************************** Listing des Players *****************************/

  // une liste avec 0 jouers
  @Test
  void getListFromGameEmptyList() {
    try (Socket socketClient = new Socket("localhost", 4141)) {
      Lobby lobby = new Lobby(socketClient);
      String messageOfNoGame = "DUNNO***";
      Assertions.assertEquals(messageOfNoGame, lobby.getListFromGame((byte) 0));

    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  // une liste avec 3 jouers
  @Test
  void getListFromGameListwithOneorLotOfElement() {
    try (Socket socketClient = new Socket("localhost", 4141)) {

      AppServeur.games.add(new Game());
      Game game = new Game();

      game.addPlayertoGame(player1);
      game.addPlayertoGame(player2);
      game.addPlayertoGame(player3);

      AppServeur.games.add(game);
      String messageOfNoGame = "LIST! 2 3***PLAYR test1***PLAYR test2***PLAYR test3***";
      Lobby lobby = new Lobby(socketClient);
      Assertions.assertEquals("DUNNO***", lobby.getListFromGame((byte) 0));
      Assertions.assertEquals("LIST! 1 0***", lobby.getListFromGame((byte) 1));
      Assertions.assertEquals(messageOfNoGame, lobby.getListFromGame((byte) 2));
    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  /*************************** Listing des Games *****************************/
  @Test
  void sendListGamesWithEmptyGames() {

    try (Socket socketClient = new Socket("localhost", 4141)) {
      Lobby lobby = new Lobby(socketClient);
      String messageOfNoGame = "GAMES 0***";
      Assertions.assertEquals(messageOfNoGame, lobby.sendListGames());
      Game game1 = new Game();
      game1.setIsStarted(true);
      Game game2 = new Game();
      game2.setIsStarted(true);
      Game game3 = new Game();
      game3.setIsStarted(true);
      Game game4 = new Game();
      game4.setIsStarted(true);
      Assertions.assertEquals(messageOfNoGame, lobby.sendListGames());

    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  @Test
  void sendListGamesWIthOneOrLotofGames() {

    try (Socket socketClient = new Socket("localhost", 4141)) {

      // une game deja commencé
      Game game1 = new Game();
      game1.setIsStarted(true);
      AppServeur.games.add(game1);

      Game game2 = new Game();
      game2.addPlayertoGame(player1);
      AppServeur.games.add(game2);

      Game game3 = new Game();
      game3.addPlayertoGame(player1);
      game3.addPlayertoGame(player2);
      game3.addPlayertoGame(player3);

      Game game4 = new Game();
      game4.addPlayertoGame(player2);
      game4.addPlayertoGame(player2);
      AppServeur.games.add(game4);

      Lobby lobby = new Lobby(socketClient);
      Assertions.assertEquals("GAMES 2***OGAME 2 1 ***OGAME 4 2 ***", lobby.sendListGames());

    } catch (Exception e) {

      e.printStackTrace();

    }
  }

} // End of class

/*
 * 
 * 
 * 
 * 
 * 
 */