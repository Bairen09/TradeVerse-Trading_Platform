package com.tradeverse.trading_service.controller;

import com.tradeverse.trading_service.dto.BuyRequest;
import com.tradeverse.trading_service.dto.SellRequest;
import com.tradeverse.trading_service.dto.TradeResponse;
import com.tradeverse.trading_service.model.Portfolio;
import com.tradeverse.trading_service.model.Transaction;
import com.tradeverse.trading_service.model.Wallet;
import com.tradeverse.trading_service.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradingController {
    private final TradingService tradingService;
    private String getUsername(){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
    @GetMapping("/wallet")
    public Wallet getWallet(){
        return tradingService.getOrCreateWallet(getUsername());
    }
    @GetMapping("/portfolio")
    public List<Portfolio>getPortfolio(){
        return tradingService.getPortfolio(getUsername());
    }
    @GetMapping("/history")
    public List<Transaction>getHistory(){
        return tradingService.getTransaction(getUsername());
    }
    @PostMapping("/buy")
    public TradeResponse buy(@RequestBody BuyRequest request){
        return tradingService.buy(getUsername(),request);
    }
    @PostMapping("/sell")
    public TradeResponse sell(@RequestBody SellRequest request){
        return tradingService.sell(getUsername(),request);
    }
}
