package org.top.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ConnectionLog {
    //формат записи даты
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final Address address;
    private final LocalDateTime startConnection;
    private final ArrayList<String> quotes = new ArrayList<>();
    private LocalDateTime stopConnection;
    private int loginAttempts = 0;

    public ConnectionLog(String ipStr, int port, LocalDateTime startConnection) {
        this.address = new Address(ipStr, port);
        this.startConnection = startConnection;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDateTime getStartConnection() {
        return startConnection;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public ArrayList<String> getQuotes() {
        return quotes;
    }

    public String getQuotesString() {
        StringBuilder sb = new StringBuilder();

        for (String quote: quotes) {
            sb.append(quote).append("\n");
        }

        return sb.toString();
    }

    public LocalDateTime getStopConnection() {
        return stopConnection;
    }

    public void addQuote(String quote) {
        quotes.add(quote);
    }

    public void setStopConnection(LocalDateTime stopConnection) {
        this.stopConnection = stopConnection;
    }

    @Override
    public String toString() {
        return "client: " + address + "\n" +
                "connection time: " + startConnection.format(DATE_TIME_FORMATTER) + "\n" +
                "failed log in attempts: " + loginAttempts + "\n" +
                "quotes set: [\n" +
                getQuotesString() + "]\n" +
                "disconnection time: " + stopConnection.format(DATE_TIME_FORMATTER) + "\n";
    }
}
