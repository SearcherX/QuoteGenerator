package org.top.account;

public class Account {
    private final int login;
    private final int password;

    public Account(String login, String password) {
        this.login = login.hashCode();
        this.password = password.hashCode();
    }

    public int getLogin() {
        return login;
    }

    public int getPassword() {
        return password;
    }
}
