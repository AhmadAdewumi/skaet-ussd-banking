package com.skaet_assessment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

//-- for history and statements
public class TransactionDtos {

    @Data
    @Builder
    public static class HistoryItem {
        private String type;        // DEPOSIT / WITHDRAWAL
        private BigDecimal amount;
        private String date;        // Formatted date string
        private String reference;
    }
}