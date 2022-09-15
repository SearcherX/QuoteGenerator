package org.top.account;

import java.util.ArrayList;

public class AccountList {
    private final ArrayList<Account> accounts = new ArrayList<>();

    public void add(String login, String password) {
        accounts.add(new Account(login, password));
    }

    //получить аккаунт по логину
    public Account getAccountByLogin(String login) {
        for (Account account: accounts) {
            if (login.hashCode() == account.getLogin())
                return account;
        }

        return null;
    }

    public void del(String login) {
        Account delAcc = getAccountByLogin(login);
        if (delAcc != null)
            accounts.remove(delAcc);
    }
}
