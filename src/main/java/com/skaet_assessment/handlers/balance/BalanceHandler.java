package com.skaet_assessment.handlers.balance;

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

@Service
public class BalanceHandler implements MenuHandler {

    private final UserService userService;
    private final WalletService walletService;
    private final SessionManager sessionManager;

    public BalanceHandler(UserService userService, WalletService walletService, SessionManager sessionManager) {
        this.userService = userService;
        this.walletService = walletService;
        this.sessionManager = sessionManager;
    }

    @Override
    public UssdState getHandlerName() {
        return UssdState.BALANCE_CHECK_PIN;
    }

    @Override
    public String handle(UssdRequest request) {
        //-- we check redis to see if we have asked for the pin
        String hasAskedForPin = sessionManager.getTempData(request.getSessionId(), "has_asked_account_pin");

        //-- If we haven't asked yet, we show the prompt
        if (hasAskedForPin == null) {
            //-- then, we set the flag in redis so next time, we know we are in Step 2
            sessionManager.saveTempData(request.getSessionId(), "has_asked_account_pin", "true");
            return "CON Enter PIN to check balance:";
        }

        String pin = request.getUserInput();

        UserDtos.UserResponse verifiedUser = userService.login(request.getPhoneNumber(), pin);

        if (verifiedUser == null) {
            sessionManager.endSession(request.getSessionId());
            return "END Invalid PIN.";
        }

        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());
        WalletDtos.BalanceResponse balance = walletService.checkBalance(user);

        sessionManager.endSession(request.getSessionId());
        return "END Your Balance is: " + balance.getFormattedBalance();
    }
}