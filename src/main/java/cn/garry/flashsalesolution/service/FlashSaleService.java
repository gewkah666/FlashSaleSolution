package cn.garry.flashsalesolution.service;

import cn.garry.flashsalesolution.entity.FlashSaleToken;
import cn.garry.flashsalesolution.entity.Transaction;
import cn.garry.flashsalesolution.entity.Transaction2FlashSaleToken;
import cn.garry.flashsalesolution.event.PaymentEvent;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleService {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleService.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Transactional
    public void prepareFlashSale(String itemId, int stock) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < stock; i++) {
            tokens.add(UUID.randomUUID().toString());
        }
        jdbcTemplate.batchUpdate("insert into flash_sale_tokens(item_id, token, status) values(?, ?, ?)", tokens.stream()
                .map(t -> {
                    Object[] args = new Object[]{itemId, t, "ACTIVE"};
                    return args;
                }).toList());
        ListOperations<String, String> ops = redisTemplate.opsForList();
        ops.rightPushAll("flash-sale-item-token-list-" + itemId, tokens);
        logger.info("{} tokens generated for item {}", stock, itemId);
    }

    @Transactional
    public String buy(String username, String itemId) {
        String token = redisTemplate.opsForList()
                .leftPop("flash-sale-item-token-list-" + itemId);
        if (token == null) {
            return "item out of stock";
        }
        Transaction transaction = transactionService.createTransaction(username, itemId);

        jdbcTemplate.update("insert into transaction_2_flash_sale_token(transaction_id, token) values(?,?)",
                transaction.getTransactionId(), token);

        return transaction.getTransactionId();
    }

    @Transactional
    @EventListener
    public void listen(PaymentEvent event) { // could be kafka or any other message queue
        Transaction2FlashSaleToken txn2Token = jdbcTemplate.queryForObject("select * from transaction_2_flash_sale_token where transaction_id = ?", Transaction2FlashSaleToken.class, event.getTransactionId());
        if (event.getStatus().equals("canceled") || event.getStatus().equals("timeout")) {
            // if the transaction is cancelled or timeout, the token should be discarded and the service will generate a new token then put it into token bucket
            if (txn2Token != null) {
                // in case the event is duplicated, get a distribution lock on the token
                RLock lock = redissonClient.getLock(txn2Token.getToken());
                try {
                    lock.lock(15, TimeUnit.SECONDS);
                    FlashSaleToken token = jdbcTemplate.queryForObject("select * from flash_sale_tokens where token = ?", FlashSaleToken.class, txn2Token.getToken());
                    if (token.getStatus().equals("active")) {
                        jdbcTemplate.update("update flash_sale_tokens set status = ? where token = ?", "inactive", token.getToken());
                        // regenerate a token
                        prepareFlashSale(token.getItemId(), 1);
                    } else { // the token has already been discarded

                    }
                } finally {
                    lock.unlock();
                }
            }
        } else if (event.getStatus().equals("paid")){
            jdbcTemplate.update("update flash_sale_tokens set status = ? where token = ?", "consumed", txn2Token.getToken());
        }
    }
}
