import client.Client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client("192.168.1.4", 1024);
        client.startClient();
    }
}
