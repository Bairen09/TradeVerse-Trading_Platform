package com.tradeverse.trading_service.service;

import com.tradeverse.trading_service.dto.BuyRequest;
import com.tradeverse.trading_service.dto.PortfolioResponse;
import com.tradeverse.trading_service.dto.SellRequest;
import com.tradeverse.trading_service.dto.TradeResponse;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingService {
    public final WalletRepository walletRepository;
    public final PortfolioRepository portfolioRepository;
    public final TransactionRepository transactionRepository;
    public final MarketService marketService;

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
    public List<PortfolioResponse> getPortfolio(String username){
        BigDecimal currentPrice= marketService.getBitcoinPriceInINR();
        return portfolioRepository.findByUsername(username)
                .stream()
                .map(p->{
                    BigDecimal investedAmount= p.getQuantity()
                            .multiply(p.getAverageBuyPrice());
                    BigDecimal currentValue= p.getQuantity().multiply(currentPrice);
                    BigDecimal profitLoss=currentValue.subtract(investedAmount);
                    return PortfolioResponse.builder()
                            .coinSymbol(p.getCoinSymbol())
                            .quantity(p.getQuantity())
                            .averageBuyPrice(p.getAverageBuyPrice())
                            .currentPrice(currentPrice)
                            .investedAmount(currentValue)
                            .currentValue(currentValue)
                            .profitLoss(profitLoss)
                            .build();
                })
                .toList();
    }
    public List<Transaction>getTransaction(String username){
        return transactionRepository.findByUsername(username);
    }
    @Transactional
    public TradeResponse buy(String username, BuyRequest request){
        if(request.getQuantity()==null|| request.getQuantity()<=0){
            throw new RuntimeException("Quantity must be greater than 0");
        }
        String coin= request.getCoinSymbol().toUpperCase();

        BigDecimal price= marketService.getBitcoinPriceInINR();
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
                            BigDecimal oldQty=existing.getQuantity();
                            BigDecimal oldAvg=existing.getAverageBuyPrice();

                            BigDecimal newQty=oldQty.add(quantity);

                            BigDecimal totalOldCost= oldQty.multiply(oldAvg);
                            BigDecimal totalNewCost= quantity.multiply(price);
                            BigDecimal newAvg=totalOldCost.add(totalNewCost).divide(newQty,8, RoundingMode.HALF_UP);
                            existing.setQuantity(newQty);
                            existing.setAverageBuyPrice(newAvg);
                            portfolioRepository.save(existing);
                        },
                        ()->{
                            Portfolio newEntry= Portfolio.builder()
                                    .username(username)
                                    .coinSymbol(coin)
                                    .quantity(quantity)
                                    .averageBuyPrice(price)
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
        return TradeResponse.builder()
                .type("BUY")
                .coinSymbol(coin)
                .quantity(quantity)
                .price(price)
                .totalAmount(totalCost)
                .walletBalance(wallet.getBalance())
                .build();
    }
    @Transactional
    public TradeResponse sell(String username, SellRequest request){
        if(request.getQuantity()==null|| request.getQuantity()<=0){
            throw new RuntimeException("Quantity must be greater than 0");
        }
        String coin= request.getCoinSymbol().toUpperCase();
        BigDecimal quantity= BigDecimal.valueOf(request.getQuantity());
        BigDecimal price= marketService.getBitcoinPriceInINR();
        Portfolio portfolio= portfolioRepository
                .findByUsernameAndCoinSymbol(username,coin)
                .orElseThrow(()->new RuntimeException("You don't own this coin"));
        //validate ownership
        if(portfolio.getQuantity().compareTo(quantity)<0){
            throw new RuntimeException("Insufficient coin quantity");
        }
        BigDecimal totalValue= price.multiply(quantity);
        Wallet wallet= getOrCreateWallet(username);
        //add money back
        wallet.setBalance(wallet.getBalance().add(totalValue));
        walletRepository.save(wallet);
        //reduce
        BigDecimal remainingQuantity= portfolio.getQuantity().subtract(quantity);
        if(remainingQuantity.compareTo(BigDecimal.ZERO)==0){
            portfolioRepository.delete(portfolio);
        }else{
            portfolio.setQuantity(remainingQuantity);
            portfolioRepository.save(portfolio);
        }
        //record
        Transaction transaction= Transaction.builder()
                .username(username)
                .coinSymbol(coin)
                .quantity(quantity)
                .price(price)
                .type("SELL")
                .timeStamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
        return TradeResponse.builder()
                .type("SELL")
                .coinSymbol(coin)
                .quantity(quantity)
                .price(price)
                .totalAmount(totalValue)
                .walletBalance(wallet.getBalance())
                .build();
    }
}
