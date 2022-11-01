package fr.parisuniversity.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {

    public static void main(String[] args) {
        String serverIpAdresse = "localhost";
        String port = "4141";
        if (args.length >= 1)
            serverIpAdresse = args[0];
        if (args.length == 2)
            if (!isValidePort(args[1])) {
                System.out.println("Ivalid Port");
                System.exit(1);
            }
        Socket socketClient = null;
        int portNum = Integer.parseInt(port);
        try {
            socketClient = new Socket(serverIpAdresse, portNum);
        } catch (IOException e) {
            System.out.println("Can Not Make Socket Communication, Please Check Your NetWork Or Server Address");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        String userName;
        while (true) {
            System.out.println("Enter Your Name :");
            scanner = sc.next();
            if (userName.length() <= 8)
                break;
        }
        while (userName.length() < 8) {
            userName = userName + '\0';
        }
        while (true) {
            System.out.println("Enter Port Number :");
            port = scanner.next();
            if (port.length() == 4)
                break;
        }
        scanner.close();
        Client client = new Client(socketClient, userName, port);

    }

    public static boolean isValidePort(String number) {
        int resultat = 0;
        if (number.length() > 4)
            return false;
        try {
            resultat = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        if (resultat < 1024) {
            return false;
        }
        return true;
    }

}
