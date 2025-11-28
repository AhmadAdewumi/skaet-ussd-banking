package com.skaet_assessment.handlers.withdraw;

import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.dto.WalletDtos;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.model.User;
import com.skaet_assessment.service.UserService;
import com.skaet_assessment.service.WalletService;
import com.skaet_assessment.session.SessionManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WithdrawPinHandler implements MenuHandler {

    private final WalletService walletService;
    private final UserService userService;
    private final SessionManager sessionManager;

    public WithdrawPinHandler(WalletService walletService, UserService userService, SessionManager sessionManager) {
        this.walletService = walletService;
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @Override
    public UssdState getHandlerName() {
        return UssdState.WITHDRAW_ENTER_PIN;
    }

    @Override
    public String handle(UssdRequest request) {
        String pin = request.getUserInput();

        //-- we retrieve the amount we saved in redis in the previous step
        String amountStr = sessionManager.getTempData(request.getSessionId(), "temp_withdraw_amount");

        if (amountStr == null) {//-- no amount found, we end the session
            sessionManager.endSession(request.getSessionId());
            return "END Session Expired. Please start again.";
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sessionManager.endSession(request.getSessionId());
            return "END System Error: Invalid Amount stored.";
        }

        //-- we verify the pin by using the login method before processing withdrawal
        UserDtos.UserResponse verifiedUserDto = userService.login(request.getPhoneNumber(), pin);

        if (verifiedUserDto == null) {
            sessionManager.endSession(request.getSessionId());
            return "END Invalid PIN. Session Terminated.";
        }

        User userEntity = userService.findUserByPhoneNumber(request.getPhoneNumber());

        try {
            WalletDtos.TransactionResponse response = walletService.withdraw(userEntity, amount);

            sessionManager.endSession(request.getSessionId());
            return "END Withdrawal Successful.\nRef: " + response.getReference() + "\nBal: " + response.getCurrency() + " " + response.getNewBalance();
        } catch (Exception e) {
            sessionManager.endSession(request.getSessionId());
            return "END Transaction Failed: " + e.getMessage();
        }
    }
}