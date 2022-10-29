package fr.parisuniversity.serveur;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;



public class Lobby implements Runnable {
    private Socket socketClient ; 
    private ByteBuffer  messages  = ByteBuffer.allocate(100); 
    private Player player =null  ;
    private Byte gameNumber ; 
    //


    public Lobby (Socket socketClient){
        this.socketClient =  socketClient ; 
    }

    public void run (){
         sendListGames();
          while(true){
            if (!saftyRead(24)){
              saftyExitPlayer();
            }
            switch (readInstruction ()){
               case "NEWPL" : 
                  String idPlayer =  new String(this.messages.array(),6,8) ; 
                  String  port =  new String(this.messages.array() ,  15 , 4) ;
                  createGame(idPlayer, port);
               break ; 
              
               case "UNREG" : 
                  unregestrationFromGame (); 
               break;
                   
               case "REGIS" :
                     idPlayer   =  new String(messages.array(),6,8);
                     port        = new String(messages.array(),15,4);
                    byte game   =   messages.get(20);
                    System.out.println("i have " + idPlayer + " " + port  + " " + game);
                    joinGame (  idPlayer  ,   port  ,  game);  
               break ; 

               case "GAME?" :
                    System.out.println(sendListGames()); 
               break ; 

               case "LIST?" : 
                   byte pnumgame =  this.messages.get(6);
                  System.out.println(getListFromGame (pnumgame));
               break;     
               
               case "START" : 
                     try {
                        initBuffer();
                        if (this.player == null){
                            messages.put(new String("DUNNO***").getBytes()); 
                            this.socketClient.getOutputStream().write(this.messages.array(), 0, 8);
                            this.socketClient.getOutputStream().flush();   
                            return ; 
                        }
                        else {
                            synchronized(AppServeur.games){
                                AppServeur.getGame(AppServeur.games, this.gameNumber).setPlayerReady(this.player, true); 
                                return;  
                            }
                        }
                     } catch (Exception e) {
                      saftyExitPlayer();
                    }
               break ; 


            } // end  of  switch
          if (this.socketClient.isClosed()){
              saftyExitPlayer();
              break  ;   
            }
        }
        
    }

   
  public String  getListFromGame (byte pgameNum){
    initBuffer();
    String messageSended =  "";  
     try {
          Game game  = AppServeur.getGame(AppServeur.games, pgameNum) ; 
        if (game==null){
            this.messages.put(new String("DUNNO***").getBytes());
            this.socketClient.getOutputStream().write(messages.array(),0,8);
            this.socketClient.getOutputStream().flush(); 
            messageSended = ("DUNNO***"); 
            return messageSended ; 
        } 

        List<Player> players  =  game.getGamePlayers(); 
        this.messages.put(new String ("LIST! ").getBytes());
        this.messages.put(pgameNum) ; 
        this.messages.put(new String(" ").getBytes()) ;
        this.messages.put((byte)players.size()) ;      
        this.messages.put(new String ("***").getBytes()); 
        this.socketClient.getOutputStream().write(messages.array(), 0, 12);
        this.socketClient.getOutputStream().flush();  
        messageSended = ("LIST! ")+String.valueOf(pgameNum)+ " "+String.valueOf(players.size())+"***";   
        for (Player player : players) {
          initBuffer();
          this.messages.put(new String ("PLAYR ").getBytes());
          this.messages.put(player.getNam().getBytes());
          this.messages.put( new String("***").getBytes());
          this.socketClient.getOutputStream().write(messages.array(),0,17);
          this.socketClient.getOutputStream().flush();  
          messageSended += ("PLAYR ");
          messageSended += (player.getNam()) + "***";
        }
      
        
     } catch (Exception e) {
            saftyExitPlayer();
    }

    return messageSended ; 
  }

   private void joinGame ( String  name  ,  String  port  ,  Byte pgamenumber){
      initBuffer();
      try { 
        if ((!iSvalidePort(port)||(name.trim().length() ==0 )||( pgamenumber >=99 )|| pgamenumber < 0 )|| (this.player!=null) ){
            this.messages.put(new String("REGNO***").getBytes());
            this.socketClient.getOutputStream().write(this.messages.array(), 0, 8);
            this.socketClient.getOutputStream().flush();  
            return ; 
        }
        synchronized (AppServeur.games){
            this.player = new Player(name, port, socketClient); 
            // VÉRIFIER SI  IL EXISTE DEJA UN  JOUER DE MEME ID 
            Game  game  =  AppServeur.getGame(AppServeur.games,pgamenumber);  
            if ( (game == null) || game.isInGamePlayers(name)){
                this.messages.put(new String("REGNO***").getBytes());
                this.socketClient.getOutputStream().write(messages.array(), 0, 8);
                this.socketClient.getOutputStream().flush();  
                this.player =null;       
             return ;
            }
            //  Si  La game qui  veut rejoindre est déja commencé ou  bien il est plaine (pas commencer)
            if ( (game.getIsStarted() )|| (!game.addPlayertoGame(this.player)) ){
                this.messages.put(new String("REGNO***").getBytes());
                this.socketClient.getOutputStream().write(messages.array(), 0, 8);
                this.socketClient.getOutputStream().flush();  
                this.player = null ;      
             return ;   
            }
            // sinon  accepter son  inscription 
            this.gameNumber =  pgamenumber ; 
            System.out.println("player " + name + " port " + port + " joined game"); 
            this.messages.put(new String("REGOK ").getBytes());
            this.messages.put(this.gameNumber) ; 
            this.messages.put (new String ("***").getBytes());
            this.socketClient.getOutputStream().write(this.messages.array(), 0, 10);
            this.socketClient.getOutputStream().flush();      
            return ; 
            

        }
        
      } catch (Exception e) {
        saftyExitPlayer();
      }
   }

   private void unregestrationFromGame (){
    initBuffer();
    try {
        if (this.player==null){
             this.messages.put(new String("DUNNO***").getBytes()); 
             this.socketClient.getOutputStream().write(this.messages.array() , 0 , 8);
             this.socketClient.getOutputStream().flush();
             return ; 
        }  
        synchronized (AppServeur.games){
            Game  game  =  AppServeur.getGame(AppServeur.games, this.gameNumber);
            if ( game == null){
                this.messages.put( new String("DUNNO***").getBytes());  
                this.socketClient.getOutputStream().write(messages.array(), 0, 8);
                this.socketClient.getOutputStream().flush();
                return ;   
            }

            if (game.removePlayer(player)){
                this.messages.put( new String("UNROK ").getBytes());  
                this.player =  null ;  
                this.messages.put(game.getnumGame()); 
                if (game.getGamePlayers().size() == 0 )
                      AppServeur.games.remove(game);
                this.messages.put(new String("***").getBytes());        
                this.socketClient.getOutputStream().write(messages.array(), 0, 10);
                this.socketClient.getOutputStream().flush();
                return ;  
            }
            this.messages.put( new String("DUNNO***").getBytes());  
            this.socketClient.getOutputStream().write(messages.array(), 0, 8);
            this.socketClient.getOutputStream().flush(); 
        }

    } catch (Exception e) {
        saftyExitPlayer();
    }

  }   
   /*
    *  Envoyer au serveur La Listes des Games qui  ne sont pas encore commencer existantes 
    */
    
  public  String  createGame (String  name , String  port){
    this.initBuffer();
    try {
        System.out.println("je suis  la dans  la createGame");
        // vérifier si  le port est valide 
        if (!iSvalidePort(port)  || (name.trim().length() ==0 ) ){
            this.messages.put(new String("REGNO***").getBytes());
            this.socketClient.getOutputStream().write(this.messages.array(), 0, 8);
            this.socketClient.getOutputStream().flush();  
            return new String  ("REGNO***"); 
        }
        // si  le jouer et deja inscrit
         if (this.player !=null){
          this.messages.put(new String("REGNO***").getBytes());
          this.socketClient.getOutputStream().write(messages.array(), 0, 8);
          this.socketClient.getOutputStream().flush();    
          return  new String  ("REGNO***"); 
        }
       synchronized( AppServeur.games){
          //pas plus de 100 parties 
          if (AppServeur.games.size()>=99){
            this.messages.put(new String("REGNO***").getBytes());
            this.socketClient.getOutputStream().write(this.messages.array(), 0, 8);
            this.socketClient.getOutputStream().flush();    
            return  new String  ("REGNO***"); 
          } 
          // creation  de La game  
          else{
              Game game =  new Game() ;  
              this.gameNumber =  game.getnumGame(); 
              this.player =  new Player(name ,  port  ,  socketClient);  
              if (game.addPlayertoGame(player)){
                  this.messages.put(new String ("REGOK ").getBytes());
                  AppServeur.games.add(game);
                  this.messages.put(game.getnumGame());
                  this.messages.put(new String ("***").getBytes());

                  this.socketClient.getOutputStream().write(this.messages.array(),0,10);
                  this.socketClient.getOutputStream().flush();   
                 return "REGOK "+ String.valueOf(game)+ "***";  
              }
             else {
                this.messages.put(new String("REGNO***").getBytes());
                this.socketClient.getOutputStream().write(this.messages.array(), 0, 8);
                this.socketClient.getOutputStream().flush();    
                return new String ("REGNO***");
             }
          }  
       }
    } catch (Exception e) {
        saftyExitPlayer();
    }

     return new String("REGNO***"); 
 }     
    /**
     * Ma .....
     * @param
     * @throws  <string>saleve</strong>exeption 
     * @return b zljeho
     */
    public String  sendListGames () {
       this.initBuffer();
       String messagesended = new String();  
       byte  nombreOfInstartedGames  =  AppServeur.getNotStartedGames(AppServeur.games); 
       this.messages.put( new String("GAMES ").getBytes());  
       this.messages.put(nombreOfInstartedGames);  
       this.messages.put (new String("***").getBytes());
       messagesended = "GAMES "+String.valueOf(nombreOfInstartedGames)+"***";
       try {
           this.socketClient.getOutputStream().write(messages.array() , 0, 10);
           this.socketClient.getOutputStream().flush();
             
       } catch (Exception e) {
        saftyExitPlayer(); 
    
        e.printStackTrace();
      }
      
       String msg = "OGAME "; 
       for (int i = 0 ; i< (byte) (AppServeur.games.size() )  ; i++){
        if (!AppServeur.games.get(i).getIsStarted()){
           this.initBuffer();
            messages.put(msg.getBytes()); 
            messages.put(AppServeur.games.get(i).getnumGame()); 
            messages.put(new String(" ").getBytes());
            messages.put((byte)AppServeur.games.get(i).getGamePlayers().size());
            messages.put(new String ("***").getBytes());
            messagesended+=msg+String.valueOf(AppServeur.games.get(i).getnumGame())+" ***";

            try {
                this.socketClient.getOutputStream().write(messages.array() ,  0 , 12);
                this.socketClient.getOutputStream().flush();
            } catch (IOException  ioe) {
                System.out.println("client disconnecter ");
                  this.saftyExitPlayer(); 
            }
        }            
    }
       return messagesended ; 
    }
    


    private String readInstruction (){
        return new String (this.messages.array(),0,5);
    }


    /*
     *   gérer le suppression  d'un utilisateur 
     */
    private synchronized void saftyExitPlayer (){

        try {
            Game currentGame  = AppServeur.getGame(AppServeur.games, gameNumber) ; 
        
            if ((this.player !=null) && ( currentGame != null ) ){
                if ( currentGame.removePlayer(this.player)){
                    this.player =  null ;  
                if  (currentGame.getGamePlayers().size() == 0 )
                     AppServeur.games.remove(currentGame) ;     
                }
            }
            
        } catch (NullPointerException e) {
           /*
            * Rien  à faire si  éxception  ses lever === l'index existe pas 
            */
        }
        finally{

            if (this.socketClient!=null){
                try {
                    this.socketClient.close();
                } catch (Exception e) {
                    System.out.println("ERROR CONNECTING TO  CLIENT SOCKET");
                }
            } 
        }
      
        return ;  
    }





   
    /*
     *  assurer de bien  lire sur le flux de la socket  tous  les octets du  protocole 
     */
    private boolean saftyRead (int size){
       this.initBuffer(); 
       try {
           /*
           *   Lire sur le flux de la socket client si  le message  contient *** du  protocole (lecture terminer)
           sinon  continuer la lecture jusqu'a soit 
           - la taille du massage depasse size   sans  trouver  le les ***  du  protcole  
           *      - trouver les  3  etoile du  protocole 
           */


        int readed =  this.socketClient.getInputStream().read(this.messages.array(), 0 , size);
        
        if ( (readed < 0)){
            saftyExitPlayer();
        }
       else {
           String stars =  new String  (this.messages.array(), readed-3,3);  
           if (stars.equals("***"))
                return  true ; 
         
           while (readed < size){
                readed  =  readed  + this.socketClient.getInputStream().read(this.messages.array(),  readed , size);  
                stars =  new String  (this.messages.array() ,  readed-3 , 3) ;
                if (stars.equals("***"))
                    return  true  ;    
            }       
        }
       } catch (IOException e) {
          return false ;  
       }   
         return  false ;  
    }



    /*
     initialisation à  Zero du buffer de communication  (messages)
    */
     public void initBuffer (){
        Arrays.fill(this.messages.array(), (byte)0);
        this.messages.clear();
    }

    
    public boolean iSvalidePort (String  number ){
        int resultat = 0 ;  
        if (number.length() > 4)
           return false ;  
        try{
            resultat = Integer.parseInt(number);
         }catch(NumberFormatException  e){
             return false ;  
         }
         if (resultat <1024 ){
           return  false ;  
         }
         return  true ;  
     }

     public void setPlayer (Player player){
        this.player =  player;
     }
  

}
