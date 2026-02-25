package com.tradeverse.trading_service.controller;

import com.tradeverse.trading_service.dto.AiRequest;
import com.tradeverse.trading_service.dto.AiResponse;
import com.tradeverse.trading_service.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    private String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/ask")
    public AiResponse ask(@RequestBody AiRequest request) {

        String answer = aiService.askAI(getUsername(), request.getQuestion());

        return AiResponse.builder()
                .answer(answer)
                .build();
    }
}
