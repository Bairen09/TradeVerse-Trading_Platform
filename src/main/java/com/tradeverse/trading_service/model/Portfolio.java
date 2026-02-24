package com.tradeverse.trading_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portfolio")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String coinSymbol;

    @Column(nullable = false)
    private Double quantity;
}
