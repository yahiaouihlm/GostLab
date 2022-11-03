package fr.parisuniversity.client;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private ByteBuffer messages = ByteBuffer.allocate(100);
    private Socket socket;
    private String port;
    private String userName;

    public Client(Socket socket, String userName, String port) {
        this.socket = socket;
        this.userName = userName;
        this.port = port;
        Scanner scanner = new Scanner(System.in);
        gameHandler();
        boolean startedFlage = false;
        while (!startedFlage) {
            /// afficher le menu de commande
            switch (scanner.next()) {
                case "NEWPL":
                    sendNewGame();
                    readServerResposeToNewAndJoinGame();
                    break;

                case "REGIS":
                    joinGame(scanner);
                    readServerResposeToNewAndJoinGame();
                    break;

                case "UNREG":
                    unregestrationFromGame();
                    break;

                case "GAME?":

                    break;
            }

        }
        scanner.close();

    }

    public void sendListGames() {
        try {
            initBuffer();
            String game_mess = "GAME?***";
            this.messages.put(game_mess.getBytes());
            this.socket.getOutputStream().write(this.messages.array(), 0, this.messages.position());
            this.socket.getOutputStream().flush();
            gameHandler();
        } catch (IOException e) {
            ToQuit();
        }

    }

    public void gameHandler() {
        try {
            readyServerResponse(10);
            byte numbersGames = this.messages.get(6);
            String message = new String(this.messages.array(), 0, 6) + numbersGames
                    + new String(this.messages.array(), 7, 3);
            System.out.println(message);
            for (int i = 0; i < numbersGames; i++) {
                readyServerResponse(12);
                byte gamesNumber = this.messages.get(6);
                byte playersNumber = this.messages.get(8);
                String OGAME_mess = readInstruction() + new String(this.messages.array(), 5, 1) + gamesNumber
                        + new String(this.messages.array(), 7, 1) + playersNumber
                        + new String(this.messages.array(), 9, 3);
                System.out.println(OGAME_mess);
            }
        } catch (Exception e) {
            ToQuit();
        }
    }

    public void unregestrationFromGame() {
        initBuffer();
        String unreg_mess = "UNREG***";
        this.messages.put(unreg_mess.getBytes());
        try {
            this.socket.getOutputStream().write(this.messages.array(), 0, this.messages.position());
            this.socket.getOutputStream().flush();
        } catch (IOException e) {
            ToQuit();
        }

        // reception de unrok ou dunno
        int readed = readyServerResponse(10);
        switch (readInstruction()) {
            case "UNROK":
                byte unreg_game_number = this.messages.get(6);
                System.out.println(
                        new String(this.messages.array(), 0, 6) + unreg_game_number
                                + new String(this.messages.array(), 7, 3));
                break;
            case "DUNNO":
                System.out.println(new String(this.messages.array(), 0, readed));
                break;
            default:
                System.out.println("message non connu :");
                System.out.println(new String(this.messages.array(), 0, readed));
                ToQuit();
                break;
        }
    }

    public void readServerResposeToNewAndJoinGame() {
        int readed = this.readyServerResponse(10);

        switch (readInstruction()) {
            case "REGNO":
                System.out.println(new String(this.messages.array(), 0, readed));
                break;
            case "REGOK":
                byte entered_game = this.messages.get(6);
                System.out.println(
                        new String(this.messages.array(), 0, 6) + entered_game
                                + new String(this.messages.array(), 7, 3));
                break;
            default:
                System.out.println("message non connu :");
                System.out.println(new String(this.messages.array(), 0, readed));
                ToQuit();
                break;
        }
    }

    public void joinGame(Scanner scanner) {
        System.out.println("Enter Game Number To Join");
        byte gameChosen = scanner.nextByte();
        /* sending data */
        initBuffer();
        this.messages.put(new String("REGIS ").getBytes());
        this.messages.put(this.userName.getBytes());
        this.messages.put(new String(" ").getBytes());
        this.messages.put(port.getBytes());
        this.messages.put(new String(" ").getBytes());
        this.messages.put(gameChosen);
        this.messages.put(new String("***").getBytes());
        try {
            this.socket.getOutputStream().write(this.messages.array(), 0, this.messages.position());
            this.socket.getOutputStream().flush();
        } catch (IOException e) {
            ToQuit();
        }
        // receiving data

    }

    public void sendNewGame() {
        this.initBuffer();
        this.messages.put(new String("NEWPL ").getBytes());
        this.messages.put(this.userName.getBytes());
        this.messages.put(new String(" ").getBytes());
        this.messages.put(port.getBytes());
        this.messages.put(new String("***").getBytes());
        try {
            this.socket.getOutputStream().write(this.messages.array(), 0, this.messages.position());
            this.socket.getOutputStream().flush();
        } catch (IOException e) {
            ToQuit();
        } // sending the NEWPL
          // message

    }

    private String readInstruction() {
        return new String(this.messages.array(), 0, 5);
    }

    private int readyServerResponse(int size) {
        this.initBuffer();
        int readed = 0;
        try {
            readed = this.socket.getInputStream().read(this.messages.array(), 0, size);
            if ((readed < 0)) {
                ToQuit();
                return readed;
            } else {
                String stars = new String(this.messages.array(), readed - 3, 3);
                if (stars.equals("***"))
                    return readed;

                while (readed < size) {
                    readed = readed + this.socket.getInputStream().read(this.messages.array(), readed, size);
                    stars = new String(this.messages.array(), readed - 3, 3);
                    if (stars.equals("***"))
                        return readed;
                }
            }
        } catch (Exception e) {
            this.ToQuit();
        }
        return readed;
    }

    private void ToQuit() {
        initBuffer();
        try {
            this.messages.put(new String("QUIT!***").getBytes());
            this.socket.getOutputStream().write(this.messages.array(), 0, 8);
            this.socket.getOutputStream().flush();
        } catch (IOException e) {

        } finally {
            // server à envoyer une mauvaise repose le client doit quitter
            System.exit(1);
        }
    }

    public void initBuffer() {
        Arrays.fill(this.messages.array(), (byte) 0);
        this.messages.clear();
    }

}
