package com.skaet_assessment.handlers.withdraw;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.session.SessionManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WithdrawHandler implements MenuHandler {

    private final SessionManager sessionManager;

    public WithdrawHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public UssdState getHandlerName() {
        return UssdState.WITHDRAW_ENTER_AMOUNT;
    }

    @Override
    public String handle(UssdRequest request) {
        String input = request.getUserInput();

        if ("2".equals(input)) {
            return "CON Enter Amount to Withdraw:";
        }

        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) return "CON Invalid Amount. Try again:";

            sessionManager.saveTempData(request.getSessionId(), "temp_withdraw_amount", input);

            sessionManager.updateState(request.getSessionId(), UssdState.WITHDRAW_ENTER_PIN);
            return "CON Enter PIN to confirm withdrawal of NGN " + input + ":";

        } catch (NumberFormatException e) {
            return "CON Invalid Amount. Try again:";
        }
    }
}