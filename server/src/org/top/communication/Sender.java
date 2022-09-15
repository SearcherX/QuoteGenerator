package org.top.communication;

import java.io.*;
import java.net.Socket;

// класс для отправки данных через сокет
public class Sender {
    private PrintWriter out = null; // поток для отправки данных

    // конструктор
    public Sender(Socket socket) throws IOException {
        out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                ),
                true
        );
    }

    // закрытие
    public void close() {
        if (out != null) {
            out.close();
            out = null;
        }
    }

    // отправка сообщения
    public void sendMsg(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
