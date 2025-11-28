package com.skaet_assessment.handlers.registration;

import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.dto.UssdRequest;
import com.skaet_assessment.enums.UssdState;
import com.skaet_assessment.handlers.MenuHandler;
import com.skaet_assessment.service.UserService;
import com.skaet_assessment.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationHandler implements MenuHandler {

    private final SessionManager sessionManager;
    private final UserService userService;

    @Override
    public UssdState getHandlerName() {
        return UssdState.REGISTER_NAME;
    }

    @Override
    public String handle(UssdRequest request) {
        UssdState currentState = sessionManager.getCurrentState(request.getSessionId());
        String input = request.getUserInput();

        //-- register name
        if (currentState == UssdState.REGISTER_NAME) {
            sessionManager.updateState(request.getSessionId(), UssdState.REGISTER_PIN);
            return "CON Welcome to Skaet Bank.\nPlease enter your Full Name:";
        }

        if (currentState == UssdState.REGISTER_PIN) {
            sessionManager.saveTempData(request.getSessionId(), "temp_name", input); //-- storing name temporarily in redis

            sessionManager.updateState(request.getSessionId(), UssdState.REGISTER_CONFIRM);
            return "CON Hello " + input + ".\nCreate a 4-digit PIN:";
        }

        //-- setup PIN
        if (currentState == UssdState.REGISTER_CONFIRM) {
            String pin = input;

            String name = sessionManager.getTempData(request.getSessionId(), "temp_name"); //--we rerieve the name

            if (name == null) {
                return "END Session Expired. Start again.";
            }

            if (pin.length() != 4 || !pin.matches("\\d+")) { //-- pin validation
                return "CON Invalid PIN. Enter 4 digits pin:";
            }

            UserDtos.RegistrationRequest regRequest = UserDtos.RegistrationRequest.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .name(name)
                    .pin(pin)
                    .build();

            userService.registerUser(regRequest);

            sessionManager.endSession(request.getSessionId());
            return "END Registration Successful! Dial code again to login.";
        }

        return "END Error in Registration.";
    }
}