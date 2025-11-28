package com.skaet_assessment.handlers.deposit;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.dto.WalletDtos;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.model.User;
import com.skaet_assessment.service.UserService;
import com.skaet_assessment.service.WalletService;
import com.skaet_assessment.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DepositConfirmHandler implements MenuHandler {

    private final WalletService walletService;
    private final UserService userService;
    private final SessionManager sessionManager;

    @Override
    public UssdState getHandlerName() {
        return UssdState.DEPOSIT_CONFIRM;
    }

    @Override
    public String handle(UssdRequest request) {
        //-- we check if user pressed "1" to confirm
        if (!"1".equals(request.getUserInput())) { //-- if not, we cancel the transaction
            sessionManager.endSession(request.getSessionId());
            return "END Transaction Cancelled.";
        }

        //-- redis stores the value as String
        String amountStr = sessionManager.getTempData(request.getSessionId(), "temp_amount");

        if (amountStr == null) {
            sessionManager.endSession(request.getSessionId());
            return "END Session Expired. Please start again.";
        }

        BigDecimal amount = new BigDecimal(amountStr);

        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());
        WalletDtos.TransactionResponse response = walletService.deposit(user, amount);

        sessionManager.endSession(request.getSessionId());
        return "END Deposit Successful.\nRef: " + response.getReference() + "\nBal: " + response.getCurrency() + " " + response.getNewBalance();
    }
}