package com.tradeverse.trading_service.service;

import com.tradeverse.trading_service.dto.BuyRequest;
import com.tradeverse.trading_service.model.Portfolio;
import com.tradeverse.trading_service.model.Transaction;
import com.tradeverse.trading_service.model.Wallet;
import com.tradeverse.trading_service.repository.PortfolioRepository;
import com.tradeverse.trading_service.repository.TransactionRepository;
import com.tradeverse.trading_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingService {
    public final WalletRepository walletRepository;
    public final PortfolioRepository portfolioRepository;
    public final TransactionRepository transactionRepository;

    public static final BigDecimal INITIAL_PRICE= BigDecimal.valueOf(100000.0);

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
    @Transactional
    public void buy(String username, BuyRequest request){
        if(request.getQuantity()==null|| request.getQuantity()<=0){
            throw new RuntimeException("Quantity must be greater than 0");
        }
        String coin= request.getCoinSymbol().toUpperCase();

        BigDecimal price= BigDecimal.valueOf(100.0);
        BigDecimal quantity= BigDecimal.valueOf(request.getQuantity());

        BigDecimal totalCost= price.multiply(quantity);
        Wallet wallet= getOrCreateWallet(username);
        if(wallet.getBalance().compareTo(totalCost)<0){
            throw new RuntimeException("Insufficient Balance");
        }
        //Deduct
        wallet.setBalance(wallet.getBalance().subtract(totalCost));
        walletRepository.save(wallet);
        //update
        portfolioRepository.findByUsernameAndCoinSymbol(username,coin)
                .ifPresentOrElse(
                        existing->{
                            existing.setQuantity(existing.getQuantity().add(quantity));
                            portfolioRepository.save(existing);
                        },
                        ()->{
                            Portfolio newEntry= Portfolio.builder()
                                    .username(username)
                                    .coinSymbol(coin)
                                    .quantity(quantity)
                                    .build();
                            portfolioRepository.save(newEntry);
                        }
                );
        //record
        Transaction transaction= Transaction.builder()
                .username(username)
                .coinSymbol(coin)
                .quantity(quantity)
                .price(price)
                .type("BUY")
                .timeStamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }
}
