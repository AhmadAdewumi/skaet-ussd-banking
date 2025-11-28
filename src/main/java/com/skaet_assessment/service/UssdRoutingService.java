package com.skaet_assessment.service;

import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.handlers.registration.RegistrationHandler;
import com.skaet_assessment.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UssdRoutingService {

    private final Map<UssdState, MenuHandler> handlers;
    private final SessionManager sessionManager;
    private final UserService userService;

    public UssdRoutingService(List<MenuHandler> handlerList, SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;

        // 1-to-1 mapping of the state and the handler itself
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(MenuHandler::getHandlerName, Function.identity()));

        //-- registration has to actually handle 3 states, but the previous impl handles 1 to 1, so we manually do that
        for (MenuHandler handler : handlerList) {
            if (handler instanceof RegistrationHandler) {
                handlers.put(UssdState.REGISTER_PIN, handler);
                handlers.put(UssdState.REGISTER_CONFIRM, handler);
            }
        }
    }

    public String processRequest(UssdRequest request) {
        UssdState currentState = sessionManager.getCurrentState(request.getSessionId());

        //-- SESSION LOGIC, check if it is a new user or an existing one
        if (currentState == null) {
            boolean userExists = userService.findUserByPhoneNumber(request.getPhoneNumber()) != null;
            if (userExists) {
                currentState = UssdState.WELCOME;
            } else {
                currentState = UssdState.REGISTER_NAME;
            }
            sessionManager.createSession(request.getSessionId(), currentState);
        }

        //-- MAIN MENU routing
        if (currentState == UssdState.WELCOME && !request.getUserInput().isEmpty()) {
            String input = request.getUserInput();
            switch (input) {
                case "1" -> {
                    currentState = UssdState.DEPOSIT_ENTER_AMOUNT;
                    sessionManager.updateState(request.getSessionId(), currentState);
                }
                case "2" -> {
                    currentState = UssdState.WITHDRAW_ENTER_AMOUNT;
                    sessionManager.updateState(request.getSessionId(), currentState);
                }
                case "3" -> {
                    currentState = UssdState.BALANCE_CHECK_PIN;
                    sessionManager.updateState(request.getSessionId(), currentState);
                }
                case "4" -> {
                    currentState = UssdState.MINI_STATEMENT;
                    sessionManager.updateState(request.getSessionId(), currentState);
                }
                case "5" -> {
                    currentState = UssdState.MULTICURRENCY;
                    sessionManager.updateState(request.getSessionId(), currentState);
                }
                default -> { return "END Invalid Option"; }
            }
        }

        //--- DELEGATION
        MenuHandler handler = handlers.get(currentState);

        if (handler == null) {
            log.error("Missing handler for state: {}", currentState);
            return "END Feature coming soon!";
        }

        return handler.handle(request);
    }
}