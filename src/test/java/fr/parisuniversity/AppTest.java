package fr.parisuniversity;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.parisuniversity.serveur.AppServeur;
import fr.parisuniversity.serveur.Game;  

/**
 * Unit test for simple App.
 */
public class AppTest 
{
   static List <Game> games =  new Vector<>();
 @BeforeAll
  public static  void init (){
        Game game1  =   new Game(); 
        Game game2  =   new Game(); 
        Game game3  =   new Game();
        game3.setIsStarted(true);
        games.add(game1); 
        games.add(game2); 
        games.add(game3);


  }


    @Test
     void getNotStartedGamesTest (){
        Assertions.assertEquals(2, AppServeur.getNotStartedGames(games));
    }

    @Test
    void getNotStartedGamesTest2 (){
        List <Game> gamess =  new Vector<>();
        gamess.add(new Game());
        Assertions.assertEquals(1, AppServeur.getNotStartedGames(gamess));
    }

    @Test
    void getNotStartedGamesTest1 (){
        List <Game> gamess =  new Vector<>();
        Game  game   =  new Game() ;
        game.setIsStarted(true);
        gamess.add(game)  ;
        Assertions.assertEquals(0, AppServeur.getNotStartedGames(gamess));
    }


 @ParameterizedTest
 @MethodSource("data")
 void getGameTest(int pInput, Game out) {
     Assertions.assertEquals(out,  AppServeur.getGame(games, pInput) );  
    }


    private static Stream<Arguments> data() {
         return Stream.of(
           Arguments.of(0, null),
           Arguments.of(1, games.get(0)),
           Arguments.of(2, games.get(1)),
           Arguments.of(3, games.get(2)),
           Arguments.of(7, null) 
        );  
    }


    public static class LobbyTest {

    }
}
