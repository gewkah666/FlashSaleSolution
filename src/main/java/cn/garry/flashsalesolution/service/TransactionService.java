package cn.garry.flashsalesolution.service;

import cn.garry.flashsalesolution.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    public Transaction createTransaction(String username, String itemId) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setUsername(username);
        transaction.setItemId(itemId);
        return transaction;
    }
}
