package org.top.log;

public record Address(String ip, int port) {

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
