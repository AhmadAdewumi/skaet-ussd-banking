package com.skaet_assessment.handlers.multiCurrency;

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

import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MultiCurrencyHandler implements MenuHandler {

    private final UserService userService;
    private final WalletService walletService;
    private final SessionManager sessionManager;

    private final Map<String, String> currencyOptions = Map.of(
            "1", "USD",
            "2", "EUR",
            "3", "GBP",
            "4", "CAD"
    );

    @Override
    public UssdState getHandlerName() {
        return UssdState.MULTICURRENCY;
    }

    @Override
    public String handle(UssdRequest request) {
        String input = request.getUserInput();

        if ("5".equals(input)) {
            return "CON Multicurrency Options:\nSelect target currency:\n1. USD\n2. EUR\n3. GBP\n4. CAD";
        }

        String targetCurrency = currencyOptions.get(input);

        if (targetCurrency == null) {
            return "CON Invalid Currency Selection.\n1. USD\n2. EUR\n3. GBP\n4. CAD";
        }

        User user = userService.findUserByPhoneNumber(request.getPhoneNumber());

        //--we call the WalletService. It handles the SOAP call internally and returns clean response
        WalletDtos.CurrencyConversionResponse response = walletService.convertBalanceToTargetCurrency(user, targetCurrency);

        //-- response formatting
        String message = String.format("END Multicurrency Balance:\n%s %s\n= %s %s\n(Rate: %s)",
                response.getOriginalCurrency(),
                response.getOriginalBalance().toPlainString(),
                response.getTargetCurrency(),
                response.getConvertedBalance().setScale(2, RoundingMode.HALF_UP),
                response.getRateUsed()
        );

        sessionManager.endSession(request.getSessionId());
        return message;
    }
}