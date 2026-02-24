package com.tradeverse.trading_service.repository;

import com.tradeverse.trading_service.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {
    List<Portfolio>findByUsername(String username);
    Optional<Portfolio>findByUsernameAndCoinSymbol(String username,String coinSymbol);
}
