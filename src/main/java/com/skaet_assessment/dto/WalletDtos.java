package com.skaet_assessment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class WalletDtos {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest { //-- since this is for payment REST API, validation is included
        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @DecimalMin(value = "1.0", message = "Minimum amount is 1.00")
        private BigDecimal amount;

        private String pin;

        @Builder.Default
        private String currency = "NGN";
    }

    @Data
    @Builder
    public static class TransactionResponse {
        private boolean success;
        private String message;
        private String reference;
        private BigDecimal newBalance;
        private String currency;
        private String transactionType; //-- DEPOSIT or WITHDRAWAL
    }

    @Data
    @Builder
    public static class BalanceResponse {
        private BigDecimal balance;
        private String currency;
        private String formattedBalance; //-- e.g NGN 5,000.00
    }

    @Data
    @Builder
    public static class CurrencyConversionResponse {
        private BigDecimal originalBalance;
        private String originalCurrency;
        private BigDecimal convertedBalance;
        private String targetCurrency;
        private BigDecimal rateUsed;
    }
}