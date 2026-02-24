package com.tradeverse.trading_service.service;

import com.tradeverse.trading_service.model.Portfolio;
import com.tradeverse.trading_service.model.Transaction;
import com.tradeverse.trading_service.model.Wallet;
import com.tradeverse.trading_service.repository.PortfolioRepository;
import com.tradeverse.trading_service.repository.TransactionRepository;
import com.tradeverse.trading_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingService {
    public final WalletRepository walletRepository;
    public final PortfolioRepository portfolioRepository;
    public final TransactionRepository transactionRepository;

    public static final double INITIAL_PRICE= 100000.0;

    public Wallet getOrCreateWallet(String username){
        return walletRepository.findByUsername(username)
                .orElseGet(()->{
                    Wallet wallet= Wallet.builder()
                            .username(username)
                            .balance(INITIAL_PRICE)
                            .build();
                    return walletRepository.save(wallet);
                });
    }
    public List<Portfolio> getPortfolio(String username){
        return portfolioRepository.findByUsername(username);
    }
    public List<Transaction>getTransaction(String username){
        return transactionRepository.findByUsername(username);
    }
}
