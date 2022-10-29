package fr.parisuniversity.serveur;

import java.net.Socket;

public class Player {


    private  int playerNumber  ; 
    private  String name ;  
    private  boolean isReady =  false ;   

    public Player( String name ,  String  port  ,  Socket socketClient){      
        this.name =  name ; 
    }
    
    
    public int getPlayerNumber() {
        return playerNumber;
    }
    public String getNam() {
        return name;
    }
    

    public boolean isReady() {
        return isReady;
    }


    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

 
}
