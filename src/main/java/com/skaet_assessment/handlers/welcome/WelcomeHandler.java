package com.skaet_assessment.handlers.welcome;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import org.springframework.stereotype.Service;

@Service
public class WelcomeHandler implements MenuHandler {

    @Override
    public UssdState getHandlerName() {
        return UssdState.WELCOME;
    }

    @Override
    public String handle(UssdRequest request) {
        return "CON Welcome back!\n" +
                "1. Deposit\n" +
                "2. Withdraw\n" +
                "3. Check Balance\n" +
                "4. Mini Statement\n" +
                "5. Multicurrency";
    }
}