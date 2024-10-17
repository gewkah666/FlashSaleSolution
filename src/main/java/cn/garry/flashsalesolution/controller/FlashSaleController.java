package cn.garry.flashsalesolution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/flash-sale")
public class FlashSaleController {


    public ResponseEntity<String> flashSale(@RequestParam String itemId) {
        return ResponseEntity.ok("");
    }
}
