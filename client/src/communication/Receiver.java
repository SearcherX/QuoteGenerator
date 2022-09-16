package communication;


import java.io.*;
import java.net.Socket;

// класс для считывания данных через сокет
public class Receiver {

    private BufferedReader in = null; // поток для чтения данных

    // конструктор
    public Receiver(Socket socket) throws IOException {
        in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                )
        );
    }

    // закрытие
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }

    // отправка сообщения
    public String receiveMsg() throws IOException {
        String msg = "";
        if (in != null) {
            msg = in.readLine();
        }
        return msg;
    }
}
