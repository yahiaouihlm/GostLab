package fr.parisuniversity.serveur;

import java.util.List;
import java.util.Vector;

public class Game {
    private final int MAXPLAYERS = 3;
    private List<Player> gamePlayers = new Vector<>();
    private byte numGame;
    public static byte idGame;

    private Boolean isStarted = false;

    public Game() {
        synchronized (this) {
            idGame++;
            numGame = idGame;
        }
    }

    public void startGame() {
        try {
            System.out.println("Game is  Started ");
        } catch (Exception e) {

            // quittez prorement la game
            e.printStackTrace();
        }
    }

    public Boolean setPlayerReady(Player pplayer, boolean ready) {
        for (Player player : gamePlayers) {
            if (player.equals(pplayer)) {
                player.setReady(ready);
                return true;
            }
            if (this.numberOfReadyPlayers() == MAXPLAYERS) {
                this.isStarted = true;
                this.startGame();
            }

        }
        return false;
    }

    public int numberOfReadyPlayers() {
        int readyPlyers = 0;
        for (Player player : gamePlayers) {
            if (player.getReady())
                readyPlyers++;
        }
        return readyPlyers;
    }

    public Boolean isInGamePlayers(String name) {
        for (Player player : gamePlayers) {
            if (player.getNam().equals(name))
                return true;
        }
        return false;
    }

    public Boolean addPlayertoGame(Player player) {
        if (this.gamePlayers.size() >= MAXPLAYERS)
            return false;
        this.gamePlayers.add(player);
        return true;
    }

    public Boolean removePlayer(Player player) {
        return this.gamePlayers.remove(player);
    }

    public Boolean getIsStarted() {
        return this.isStarted;
    }

    public void setIsStarted(Boolean isStarted) {
        this.isStarted = isStarted;
    }

    public List<Player> getGamePlayers() {
        return gamePlayers;
    }

    public byte getnumGame() {
        return numGame;
    }

    @Override
    public String toString() {
        return "Game" + String.valueOf(numGame);
    }

}
