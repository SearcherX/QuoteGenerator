package org.top.server;


import org.top.communication.Receiver;
import org.top.communication.Sender;
import org.top.log.ConnectionLog;
import org.top.log.FileLog;
import org.top.quotagen.IGenerator;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

// класс-обработчик одного клиента
// запускается в отдельном потоке и работает с клиентом
public class ClientProcessor {

    // поля
    private boolean isFree;
    public boolean isFree() {
        return isFree;
    }

    private Socket remoteClient;
    private IGenerator generator;

    // конструктор
    public ClientProcessor(IGenerator generator) {
        isFree = true;
        remoteClient = null;
        this.generator = generator;
    }

    // подготовка работы с клиентом
    public void prepareClient(Socket socket) throws Exception {
        if (!isFree) {
            throw new Exception("server: not free clientProcessor!");
        }
        remoteClient = socket;
        isFree = false;
    }

    // работа с клиентом
    public void processClient(ConnectionLog log, int quotesLimit) throws IOException {
        Sender sender = null;
        Receiver receiver = null;
        boolean stop = false;

        try {
            // объекты для отправки и получения данных
            sender = new Sender(remoteClient);
            receiver = new Receiver(remoteClient);

            sender.sendMsg("server: welcome to quote generator user: " +
                    remoteClient.getInetAddress() + ":" + remoteClient.getPort());

            //проверка логина
            while (true) {
                if (log.getLoginAttempts() == 3) {
                    stop = true;
                    sender.sendMsg("server: you have no more attempts");
                    sender.sendMsg("server: bye");
                    log.setStopConnection(LocalDateTime.now());
                    FileLog.writeLog(log.toString());
                    break;
                }
                sender.sendMsg("server: enter login: ");
                String login = receiver.receiveMsg();

                log.setLoginAttempts(log.getLoginAttempts() + 1);
                if (login.equals("user1")) {
                    break;
                } else {
                    sender.sendMsg("server: wrong login. Try again");
                }
            }

            if (!stop) {
                //проверка пароля
                while (true) {
                    if (log.getLoginAttempts() == 3) {
                        stop = true;
                        sender.sendMsg("server: you have no more attempts. Max attempts - 3");
                        sender.sendMsg("server: bye");
                        log.setStopConnection(LocalDateTime.now());
                        FileLog.writeLog(log.toString());
                        break;
                    }
                    sender.sendMsg("server: enter password: ");
                    String pass = receiver.receiveMsg();

                    log.setLoginAttempts(log.getLoginAttempts() + 1);
                    if (pass.equals("123")) {
                        break;
                    } else {
                        sender.sendMsg("server: wrong password. Try again");
                    }
                }
            }

            if (!stop) {
                sender.sendMsg("server: logged in successfully");
                int i = 1;
                // цикл работы с клиентом
                while (true) {
                    // TODO: добавить логи работы с клиентом
                    // 1. читаем сообщение
                    String msg = receiver.receiveMsg();

                    // 2. анализируем сообщение
                    if (msg.equals("quote")) {
                        if (log.getQuotes().size() == quotesLimit) {
                            sender.sendMsg("server: you have exceeded the limit of quotes");
                            sender.sendMsg("server: bye");
                            log.setStopConnection(LocalDateTime.now());
                            FileLog.writeLog(log.toString());
                            break;
                        }
                        // то отправить цитату
                        String quote = generator.getRandomQuota();
                        //добавить в лог цитату, отправленную клиенту
                        sender.sendMsg("server: " + i++ + ". " + quote);
                        log.addQuote(quote);
                    } else if (msg.equals("exit")) {
                        sender.sendMsg("server: bye");
                        //добавить в лог время отсоединения клиента
                        log.setStopConnection(LocalDateTime.now());
                        FileLog.writeLog(log.toString());
                        break;
                    } else {
                        sender.sendMsg("server: invalid command");
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println("server: something wrong during processing client: " + ex.getMessage());
        }
        finally {
            // по окончанию цикла
            if (sender != null) {
                sender.close();
            }
            if (receiver != null) {
                receiver.close();
            }
            if (remoteClient != null && !remoteClient.isClosed()) {
                remoteClient.close();
            }

            // освободить исполнителя
            remoteClient = null;
            isFree = true;
        }
    }
}
