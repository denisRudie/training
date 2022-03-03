package moneytransfer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.nonNull;

public class TransferImpl {

    /**
     * key: account name or id
     * value: account object
     */
    private Map<String, Account> accounts = new ConcurrentHashMap<>();

    void addAccount(String accountName) {
        // TODO: 03.03.2022 validation
        accounts.putIfAbsent(accountName, new Account(accountName));
    }

    void deposit(String toAccount, Long amount) {
        // TODO: 03.03.2022 validate account existing and amount are positive
        if (nonNull(amount) && amount.compareTo(0L) > 0) {
            Account account = accounts.get(toAccount);
            account.money.addAndGet(amount);
        }
    }

    void transfer(String from, String to, Long amount) {
        // TODO: 03.03.2022 validation
        Account fromAcc = accounts.get(from);
        long amountBeforeTransfer = fromAcc.money.getAndUpdate(currentAmount -> currentAmount >= amount ? currentAmount - amount : currentAmount);
        if (amountBeforeTransfer > amount) {
            Account toAccount = accounts.get(to);
            toAccount.money.addAndGet(amount);
        }
    }

    static class Account {
        String name;
        AtomicLong money;

        public Account(String name) {
            this.name = name;
            money = new AtomicLong();
        }
    }
}
