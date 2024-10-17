package cn.garry.flashsalesolution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class FlashSaleService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    ScheduledExecutorService service = Executors.newScheduledThreadPool(128);

    public String buy(String username, String itemId) {
        String token = redisTemplate.opsForList()
                .leftPop("flash-sale-item-token-list-" + itemId);
        if (token == null) {
            return "no enough items";
        }
        return createTransactionId(username, itemId);
    }

    private String createTransactionId(String username, String itemId) {
        String transactionId = UUID.randomUUID().toString();
        return transactionId;
    }


}
