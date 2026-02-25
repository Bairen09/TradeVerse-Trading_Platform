package com.tradeverse.trading_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {
    private final RestTemplate restTemplate;
    private final TradingService tradingService;

    public String askAI(String username, String question){
        var portfolio= tradingService.getPortfolio(username);
        StringBuilder context= new StringBuilder("User portfolio:\n");
        portfolio.forEach(p->
                context.append(p.getCoinSymbol())
                        .append("- Qty: ")
                        .append(p.getQuantity())
                        .append(", P/L: ")
                        .append(p.getProfitLoss())
                        .append("\n")
        );
        String prompt = context + "\nUser question: " + question;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3");
        body.put("prompt", prompt);
        body.put("stream", false);

        Map response = restTemplate.postForObject(
                "http://localhost:11434/api/generate",
                body,
                Map.class
        );

        return (String) response.get("response");
    }
}
