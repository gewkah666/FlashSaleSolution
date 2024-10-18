package cn.garry.flashsalesolution.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class FlashSaleServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleServiceTest.class);
    ExecutorService executorService = Executors.newFixedThreadPool(100);
    @Autowired
    private FlashSaleService flashSaleService;

    @Test
    public void test() {
        String itemId = "123456";
        flashSaleService.prepareFlashSale(itemId, 5);
        for (int i = 0; i < 100; i++) {
            String username = "user" + i;
            executorService.submit(() -> {
                String result = flashSaleService.buy(username, itemId);
                logger.info("{}'s flash sale result: {}", username, result);
            });
        }
    }
}
