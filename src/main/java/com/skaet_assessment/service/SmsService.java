package com.skaet_assessment.service;

import com.skaet_assessment.events.TransactionEvent;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//-- Using twilio to send SMS
@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromNumber;

//    @PostConstruct
    public void init() {
        //-- We initialize the Twilio SDK at  app startup
        if (accountSid != null && !accountSid.isEmpty()) {
//            Twilio.init(accountSid, authToken);
            log.info("Twilio SDK Initialized successfully with Account SID: {}", accountSid);
        } else {
            log.warn("Twilio credentials are not set. SMS sending will fail.");
        }
    }

    @Async
    @EventListener
    public void sendRealSms(TransactionEvent event) {
        log.info("Async Event: Preparing to send SMS to {}...", event.getPhoneNumber());

        try {
//            Message message = Message.creator(
//                    new PhoneNumber(event.getPhoneNumber()),
//                    new PhoneNumber(fromNumber),
//                    event.getMessage()
//            ).create();

//            log.info("SMS SENT SUCCESSFULLY! SID: {}, Status: {}", message.getSid(), message.getStatus());

        } catch (Exception e) {
            log.error("SMS FAILED: Could not send to {}. Error: {}", event.getPhoneNumber(), e.getMessage());
        }
    }
}