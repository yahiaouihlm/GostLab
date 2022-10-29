package fr.parisuniversity.serveur;
import java.util.List;
import java.util.Vector;

public class Game {
    

    private List<Player> gamePlayers =  new Vector<>();   
    private byte numGame ;  
    private static byte idGame ; 
    private Boolean isStarted =  false  ;  
  


    public  Game () {
       synchronized (this){
          idGame++;
          numGame = idGame;  
       }
    }
    
    
    public  Boolean setPlayerReady (Player pplayer  , boolean ready){
        for (Player player : gamePlayers) {
             if (player.equals(pplayer)){
                   player.setReady(ready);
                   return true ;  
                }
        }
         return  false ; 
    }
      
       

    public  Boolean isInGamePlayers ( String  name ){
        for (Player player : gamePlayers) {
             if (player.getNam().equals(name))
                  return  true ;    
       }
       return  false ; 
    }
   
    public  Boolean addPlayertoGame(Player player){
        if (this.gamePlayers.size() >=3 )
                return false  ;  
        this.gamePlayers.add(player);  
      return true ;  
    }

    public Boolean removePlayer(Player player ){
    	return  this.gamePlayers.remove(player);
    }



    public Boolean getIsStarted() {
        return isStarted;
    }


    public void setIsStarted(Boolean isStarted) {
        this.isStarted = isStarted;
    }




    
    public List<Player> getGamePlayers() {
        return gamePlayers;
    }

    public byte getnumGame() {
        return  numGame;
    }


     @Override
     public String toString (){
        return "Game"+String.valueOf(numGame);
     }
   
 
}
