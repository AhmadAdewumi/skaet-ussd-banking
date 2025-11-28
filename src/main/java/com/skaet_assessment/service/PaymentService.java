package com.skaet_assessment.service;

import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.dto.WalletDtos;
import com.skaet_assessment.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


//-- for the payment REST API
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserService userService;
    private final WalletService walletService;

    public WalletDtos.TransactionResponse processDeposit(WalletDtos.PaymentRequest request) {
        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());
        if (user == null) {
            throw new IllegalArgumentException("User not found with phone: " + request.getPhoneNumber());
        }

        return walletService.deposit(user, request.getAmount());
    }

    public WalletDtos.TransactionResponse processWithdrawal(WalletDtos.PaymentRequest request) {
        UserDtos.UserResponse verifiedUser = userService.login(request.getPhoneNumber(), request.getPin());
        if (verifiedUser == null) {
            throw new SecurityException("Invalid Credentials (PIN or Phone Number incorrect)");
        }

        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());

        return walletService.withdraw(user, request.getAmount());
    }
}