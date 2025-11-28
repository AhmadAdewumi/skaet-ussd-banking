package com.skaet_assessment.controller;

import com.skaet_assessment.dto.UssdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.skaet_assessment.service.UssdRoutingService;

@RestController
@RequestMapping("/api/ussd")
@RequiredArgsConstructor
public class UssdController {
    private final UssdRoutingService ussdRoutingService;

    @PostMapping
    public String handleUssdRequest(@RequestParam String sessionId, @RequestParam String serviceCode,
                                    @RequestParam String phoneNumber, @RequestParam(defaultValue = "") String text) {

        UssdRequest request = UssdRequest.builder()
                .sessionId(sessionId)
                .serviceCode(serviceCode)
                .phoneNumber(phoneNumber)
                .text(text)
                .build();

        return ussdRoutingService.processRequest(request);
    }
}