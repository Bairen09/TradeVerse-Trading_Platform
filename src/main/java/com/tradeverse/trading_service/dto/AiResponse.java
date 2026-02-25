package com.tradeverse.trading_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiResponse {
    private String answer;
}
