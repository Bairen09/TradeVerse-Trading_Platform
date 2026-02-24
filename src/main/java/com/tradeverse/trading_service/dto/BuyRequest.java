package com.tradeverse.trading_service.dto;

import lombok.Data;

@Data
public class BuyRequest {
    private String coinSymbol;
    private Double quantity;
}
