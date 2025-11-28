package com.skaet_assessment.handlers.deposit;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DepositHandler implements MenuHandler {

    private final SessionManager sessionManager;

    @Override
    public UssdState getHandlerName() {
        return UssdState.DEPOSIT_ENTER_AMOUNT;
    }

    @Override
    public String handle(UssdRequest request) {
        String input = request.getUserInput();

        if ("1".equals(input)) {
            return "CON Enter Amount to Deposit:";
        }

        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) return "CON Invalid Amount. Try again:";

            sessionManager.saveTempData(request.getSessionId(), "temp_amount", input);

            sessionManager.updateState(request.getSessionId(), UssdState.DEPOSIT_CONFIRM);
            return "CON Deposit NGN " + input + "?\n1. Confirm\n2. Cancel";

        } catch (NumberFormatException e) {
            return "CON Invalid Amount. Try again:";
        }
    }
}