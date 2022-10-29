package fr.parisuniversity;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.parisuniversity.serveur.AppServeur;
import fr.parisuniversity.serveur.Game;
import fr.parisuniversity.serveur.Lobby;
import fr.parisuniversity.serveur.Player;

public class LobbyTest {
    
        /********************************** Simulation Serveur ***************************************/
 
  Socket  socketClient ;
 @BeforeEach
 void init () throws UnknownHostException, IOException{ 
   AppServeur.games.clear();
   socketClient =  new Socket("localhost", 4141);    
    }

  @AfterEach
  void initListAndSocket(){
    AppServeur.games.clear();
  }
 

    
    @Test
    void getListFromGameTestEmptyListPlayer() throws IOException{      
        try {
            socketClient =  new Socket("localhost", 4141);      
        } catch (Exception e) {
            e.printStackTrace();
        } 
        Lobby lobbygame = new Lobby(socketClient);
         Assertions.assertEquals("DUNNO***",lobbygame.getListFromGame((byte)1));    
    
        }
    
    
    
    
    @Test 
  void getListFromGameTestFullListPlayer()throws Exception {  
            Lobby  lobby =  new Lobby( socketClient );        
            String functionreturn = "LIST! 1 3***PLAYR player1***PLAYR player2***PLAYR player3***";
            Game game =  new Game() ; 
            game.addPlayertoGame(new Player("player1", "3312", null));
            game.addPlayertoGame(new Player("player2", "2432", null));
            game.addPlayertoGame(new Player("player3", "3432", null));
            AppServeur.games.add(game);
            System.out.println("Cest ça " + game);
            Assertions.assertEquals(functionreturn,lobby.getListFromGame((byte)1) );
            Assertions.assertEquals("DUNNO***",lobby.getListFromGame((byte)4) );
            
            }
            
            
            
            
            
    @Test
    void sendListGamesTestEmptyListGames () throws Exception{
            try {
                socketClient =  new Socket("localhost", 4141);    
                Lobby  lobbygame =  new Lobby( socketClient );   
                  Assertions.assertEquals("GAMES 0***",lobbygame.sendListGames() );   
            } catch (Exception e) {
                e.printStackTrace();
            }    
     }
        
   
 
     @Test
     void sendListGamesTestListGames (){
                 try {
                     socketClient =  new Socket("localhost", 4141);    
                     Lobby  lobbygame =  new Lobby( socketClient );       
                     String resultmessage =  "GAMES 3***OGAME 1 ***OGAME 3 ***OGAME 4 ***";
                     AppServeur.games.add(new Game());
                     Game game2 =  new Game();
                     game2.setIsStarted(true);
                     AppServeur.games.add(game2);
                     AppServeur.games.add(new Game());
                     AppServeur.games.add(new Game());
                     Assertions.assertEquals(resultmessage,lobbygame.sendListGames() );    
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
        }
        
    
     // CREATE GAME Test 
  /* 
  @Test 
  void createGameTestInvalideportOrName (){
    Thread th = new Thread( new Runnable()  {
        public void  run (){   
            try {
                Lobby lobbygame  =  new Lobby(new Socket("localhost" ,4141));
                String resultmessage =  "REGNO***";
                Assertions.assertEquals(resultmessage,lobbygame.createGame(null, null) );    
                Assertions.assertEquals(resultmessage,lobbygame.createGame("test1", null) );    
                Assertions.assertEquals(resultmessage,lobbygame.createGame(null, "test2") );    
            }
               catch (Exception e) {
                  e.printStackTrace();
              }
        }
    });
    th.start();
  }


  @Test 
  void createGameTestNotNullPlayer(){
    Thread th = new Thread( new Runnable()  {
        public void  run (){   
            try {
                Lobby lobbygame  =  new Lobby(new Socket("localhost" ,4141));
                String resultmessage =  "REGNO***";
                //lobbygame.setPlayer(new Player(null, null, null));      
                Assertions.assertEquals("resultmessage",lobbygame.createGame("test", "4141") );   
            }
               catch (Exception e) {
                  e.printStackTrace();
              }
        }
    });
    th.start();
  }
/* 
  @Test
  void createGameTestFullGameList (){
    Thread th = new Thread( new Runnable()  {
        public void  run (){   
            try {
                for (int i=0 ;  i<999 ; i++){
                    AppServeur.games.add(new Game());
                }
                Lobby lobbygame  =  new Lobby(new Socket("localhost" ,4141));
                String resultmessage =  "REGNO***";
                Assertions.assertEquals(resultmessage,lobbygame.createGame(null, "4141") );  
              
            }
               catch (Exception e) {
                  e.printStackTrace();
              }
        }
    });
    th.start(); 
  }
*/
 /*
  * 
     Lobby lobbygame  =  new Lobby(new Socket("localhost" ,4141));
                String resultmessage =  "REGNO***";
                lobbygame.setPlayer(new Player(null, null, null));      
                Assertions.assertEquals(resultmessage,lobbygame.createGame(null, "4141") );   

  */

 } // End of  class 

