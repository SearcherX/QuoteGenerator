package org.top.server;

import org.top.Main;
import org.top.communication.Sender;
import org.top.log.ConnectionLog;
import org.top.quotagen.IGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Executable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

// Класс Сервера - реализует логику сервера
public class Server {

    // поля
    private static Logger logger;
    private final String ipStr;           // адрес сервера
    private final int port;               // порт сервера
    private final int clientsLimit;              // максимльное кол-во входящих подключений
    private final int quotesLimit;
    private IGenerator generator;   // генератор цитат
    private boolean isStarted;      // запущен ли

    private final ClientProcessor[] processors; // массив обработчиков клиентов
    ExecutorService threadPool = null; // пул потоков

    //лог соединений
    private final ArrayList<ConnectionLog> logs = new ArrayList<>();

    // конструктор с параметрами
    public Server(String ipStr, int port, int clientsLimit, int quotesLimit, IGenerator generator) {
        this.ipStr = ipStr;
        this.port = port;
        this.clientsLimit = clientsLimit + 1;     // одно подключение для спецаильных ответов
        this.quotesLimit = quotesLimit;
        this.generator = generator;

        // создадим обработчики
        threadPool = Executors.newFixedThreadPool(clientsLimit);   // пул потоков
        processors = new ClientProcessor[clientsLimit];    // не увеличинный лимит
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new ClientProcessor(generator);  // создали пустые обработчики
        }
    }

    /* Client Client null null null */
    /*
        Алгоритм работы сервера:
            - метод сервера ожидает входящие подключения
            - при подключении очередного клиента, сервер помещает его в список клиентов
            - и запускает метод работы с клиентом в отдельном потоке
            - при отключении клиента он удаляется из списка
     */

    // метод работы сервера
    public void run() throws IOException {
        ServerSocket server = null; // сокет сервера

        try {
            System.out.println(getPrefix() + " starting server ...");
            server = new ServerSocket(port, clientsLimit, InetAddress.getByName(ipStr));

            // цикл работы сервера: подключать клиентов и запускать потоки на них
            while (true) {
                System.out.println(getPrefix() + " waiting connection ...");
                Socket nextClient = server.accept();    // тут подключился очередной клиент

                // получить свободного исполнителя
                ClientProcessor processor = getFreeProcessor();
                if (processor != null) {
                    // если есть свободный, то запустить его
                    // создать лог соединения клиента
                    ConnectionLog log = new ConnectionLog(nextClient.getInetAddress().toString(), nextClient.getPort(),
                            LocalDateTime.now());
                    processor.prepareClient(nextClient);
                    logs.add(log);
                    threadPool.execute(() -> {
                        try {
                            processor.processClient(log, quotesLimit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // если нет свободного обработчика
                    Sender sender = new Sender(nextClient);
                    sender.sendMsg("server: the server is under maximum load. Please try to connect later");
                    sender.close();
                    nextClient.close();
                }
            }
        }
        catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
        finally {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        }
    }

    // метод получения свободного исполнителя
    private ClientProcessor getFreeProcessor() {
        for (ClientProcessor processor: processors) {
            if (processor.isFree()) {
                return processor;
            }
        }
        return null;
    }

    // вспомогательный метод префикса сервера
    private String getPrefix() {
        return "server " + ipStr + ":" + port + "> ";
    }
}
