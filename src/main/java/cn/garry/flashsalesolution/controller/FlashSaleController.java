package cn.garry.flashsalesolution.controller;

import cn.garry.flashsalesolution.service.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/flash-sale")
public class FlashSaleController {
    @Autowired
    private FlashSaleService flashSaleService;

    @PostMapping
    public ResponseEntity<String> flashSale(@RequestParam String itemId) {
        return ResponseEntity.ok(flashSaleService.buy("user", itemId));
    }
}
