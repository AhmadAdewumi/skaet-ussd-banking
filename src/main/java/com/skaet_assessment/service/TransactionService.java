package com.skaet_assessment.service;

import com.skaet_assessment.dto.TransactionDtos;
import com.skaet_assessment.mapper.EntityMapper;
import com.skaet_assessment.model.Transaction;
import com.skaet_assessment.model.User;
import com.skaet_assessment.model.Wallet;
import com.skaet_assessment.repository.TransactionRepository;
import com.skaet_assessment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<TransactionDtos.HistoryItem> getMiniStatement(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("User has no wallet"));

        List<Transaction> transactions = transactionRepository.findByWalletOrderByCreatedAtDesc(
                wallet,
                PageRequest.of(0, 5)
        );

        return transactions.stream()
                .map(entityMapper::toHistoryItem)
                .collect(Collectors.toList());
    }
}