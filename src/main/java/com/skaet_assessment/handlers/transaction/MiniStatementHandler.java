package com.skaet_assessment.handlers.transaction;

import com.skaet_assessment.dto.TransactionDtos;
import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.model.User;
import com.skaet_assessment.service.TransactionService;
import com.skaet_assessment.service.UserService;
import com.skaet_assessment.session.SessionManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiniStatementHandler implements MenuHandler {

    private final TransactionService transactionService;
    private final UserService userService;
    private final SessionManager sessionManager;

    public MiniStatementHandler(TransactionService transactionService, UserService userService, SessionManager sessionManager) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @Override
    public UssdState getHandlerName() {
        return UssdState.MINI_STATEMENT;
    }

    @Override
    public String handle(UssdRequest request) {
        //== we check if we have asked for a PIN
        String hasAskedForPin = sessionManager.getTempData(request.getSessionId(), "has_asked_account_pin");

        if (hasAskedForPin == null) {
            sessionManager.saveTempData(request.getSessionId(), "has_asked_account_pin", "true");
            return "CON Enter PIN to view Mini Statement:";
        }

        String pin = request.getUserInput();
        UserDtos.UserResponse verifiedUser = userService.login(request.getPhoneNumber(), pin);

        if (verifiedUser == null) {
            sessionManager.endSession(request.getSessionId());
            return "END Invalid PIN. Access Denied.";
        }
        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());
        List<TransactionDtos.HistoryItem> history = transactionService.getMiniStatement(user);

        if (history.isEmpty()) {
            sessionManager.endSession(request.getSessionId());
            return "END No transactions found.";
        }

        //-- FORMATTER
        StringBuilder sb = new StringBuilder("END Mini Statement:\n");
        for (TransactionDtos.HistoryItem item : history) {
            String typeName = item.getType(); //-- WITHDRAW/DEPOSIT
            String shortType = typeName.length() >= 3 ? typeName.substring(0, 3) : typeName; //-- jut being a little bit defensive

            sb.append(shortType) //-- DEP or WIT
                    .append(" ")
                    .append(item.getAmount())
                    .append("\n");
        }

        sessionManager.endSession(request.getSessionId());
        return sb.toString();
    }
}