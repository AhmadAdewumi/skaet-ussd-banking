package com.skaet_assessment.service;

import com.skaet_assessment.dto.WalletDtos;
import com.skaet_assessment.enums.TransactionType;
import com.skaet_assessment.events.TransactionEvent;
import com.skaet_assessment.mapper.EntityMapper;
import com.skaet_assessment.model.Transaction;
import com.skaet_assessment.model.User;
import com.skaet_assessment.model.Wallet;
import com.skaet_assessment.repository.TransactionRepository;
import com.skaet_assessment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final EntityMapper entityMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ExchangeRateService exchangeRateService;

    public WalletDtos.BalanceResponse checkBalance(User user) {
        Wallet wallet = getWallet(user);
        return entityMapper.toBalanceResponse(wallet);
    }

    @Transactional
    public WalletDtos.TransactionResponse deposit(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = getWallet(user);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction txn = recordTransaction(wallet, amount, TransactionType.DEPOSIT, "USSD Deposit");

        String msg = String.format("Credit Alert: NGN %s deposited. Ref: %s. New Bal: %s", amount, txn.getReference(), wallet.getBalance());

        //-- we publish the event, fire and forget
        eventPublisher.publishEvent(new TransactionEvent(this, user.getPhoneNumber(), msg, amount));

        return entityMapper.toTransactionResponse(txn, wallet);
    }

    @Transactional
    public WalletDtos.TransactionResponse withdraw(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = getWallet(user);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction txn = recordTransaction(wallet, amount, TransactionType.WITHDRAWAL, "USSD Withdrawal");

        String msg = String.format("Debit Alert: NGN %s withdrawn. Ref: %s. New Bal: %s", amount, txn.getReference(), wallet.getBalance());

        //-- we publish the event, fire and forget
        eventPublisher.publishEvent(new TransactionEvent(this, user.getPhoneNumber(), msg, amount));

        return entityMapper.toTransactionResponse(txn, wallet);
    }

    public WalletDtos.CurrencyConversionResponse convertBalanceToTargetCurrency(User user, String targetCurrency) {
        Wallet wallet = getWallet(user);
        BigDecimal balanceInNaira = wallet.getBalance();

        BigDecimal rate = exchangeRateService.getExchangeRate("NGN", targetCurrency);

        BigDecimal convertedAmount = balanceInNaira.multiply(rate);

        return WalletDtos.CurrencyConversionResponse.builder()
                .originalBalance(balanceInNaira)
                .originalCurrency("NGN")
                .convertedBalance(convertedAmount)
                .targetCurrency(targetCurrency)
                .rateUsed(rate)
                .build();
    }

    private Wallet getWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("User has no wallet"));
    }

    private Transaction recordTransaction(Wallet wallet, BigDecimal amount, TransactionType type, String desc) {
        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(type)
                .reference(UUID.randomUUID().toString())
                .description(desc)
                .build();
        return transactionRepository.save(txn);
    }
}