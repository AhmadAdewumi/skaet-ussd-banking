package com.skaet_assessment.model;

import com.skaet_assessment.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

//-- making most classes not updatable as this is history and it should be immutable
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_txn_reference", columnList = "reference")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Column(unique = true, nullable = false, updatable = false)
    private String reference;

    @Column(updatable = false)
    private String description;
}
