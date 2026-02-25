package com.tradeverse.trading_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final RestTemplate restTemplate;
    public BigDecimal getBitcoinPriceInINR(){
        String url="https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=inr";
        Map response= restTemplate.getForObject(url, Map.class);
        Map bitcoin=(Map) response.get("bitcoin");
        Number price=(Number) bitcoin.get("inr");
        return BigDecimal.valueOf(price.doubleValue());
    }
}
