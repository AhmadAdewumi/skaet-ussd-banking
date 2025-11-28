package com.skaet_assessment.mapper;

import com.skaet_assessment.dto.TransactionDtos;
import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.dto.WalletDtos;
import com.skaet_assessment.model.Transaction;
import com.skaet_assessment.model.User;
import com.skaet_assessment.model.Wallet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EntityMapper {
    //-- performance optimization, creation every single time is expensive, we create once and re-use
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public UserDtos.UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return UserDtos.UserResponse.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .build();
    }

    public WalletDtos.TransactionResponse toTransactionResponse(Transaction txn, Wallet wallet) {
        return WalletDtos.TransactionResponse.builder()
                .success(true)
                .message(txn.getDescription())
                .reference(txn.getReference())
                .newBalance(wallet.getBalance())
                .currency(wallet.getCurrencyCode())
                .transactionType(txn.getType().name())
                .build();
    }

    public WalletDtos.BalanceResponse toBalanceResponse(Wallet wallet) {
        return WalletDtos.BalanceResponse.builder()
                .balance(wallet.getBalance())
                .currency(wallet.getCurrencyCode())
                .formattedBalance(wallet.getCurrencyCode() + " " + wallet.getBalance())
                .build();
    }

    public TransactionDtos.HistoryItem toHistoryItem(Transaction txn) {
        return TransactionDtos.HistoryItem.builder()
                .type(txn.getType().name())
                .amount(txn.getAmount())
                .reference(txn.getReference())
                .date(txn.getCreatedAt().format(DATE_FMT))
                .build();
    }
}