package com.tradeverse.trading_service.dto;

import lombok.Data;

@Data
public class SellRequest {
    private String coinSymbol;
    private Double quantity;
}
