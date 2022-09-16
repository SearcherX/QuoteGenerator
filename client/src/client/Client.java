package client;

import communication.Receiver;
import communication.Sender;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final String serverIpStr;
    private final int serverPort;

    public Client(String serverIpStr, int serverPort) {
        this.serverIpStr = serverIpStr;
        this.serverPort = serverPort;
    }

    public void startClient() throws IOException {
        Socket client = null;   // сокет клиента
        Sender sender = null;
        Receiver receiver = null;

        try {
            client = new Socket(serverIpStr, serverPort);
            System.out.println("Client created and connected to remote server");

            // объекты для отправки и получения данных
            sender = new Sender(client);
            receiver = new Receiver(client);
            Scanner in = new Scanner(System.in);

            // цикл работы клиента: пока не получит bye от сервера
            while (true) {
                String serverMsg = receiver.receiveMsg();

                if (serverMsg.equals("server> enter login: ")) {
                    System.out.print(serverMsg);
                    String login = in.next();
                    sender.sendMsg(login);
                } else if (serverMsg.equals("server> enter password: ")) {
                    System.out.print(serverMsg);
                    String password = in.next();
                    sender.sendMsg(password);
                } else if (serverMsg.equals("server> bye")) {
                    System.out.println(serverMsg);
                    break;
                } else if (serverMsg.contains("server>")
                        && !serverMsg.contains("server> welcome to quote generator")
                        && !serverMsg.contains("attempts")) {
                    System.out.println(serverMsg);
                    String command = in.next();
                    sender.sendMsg(command);
                } else {
                    System.out.println(serverMsg);
                }
            }
        } catch (Exception ex) {
            System.out.println("Something wrong: " + ex.getMessage());
        } finally {
            if (client != null && !client.isClosed()) {
                client.close();
            }
        }
    }
}
