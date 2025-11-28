package com.skaet_assessment.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;


@Getter
public class TransactionEvent extends ApplicationEvent {
    private final String phoneNumber;
    private final String message;
    private final BigDecimal amount;

    public TransactionEvent(Object source, String phoneNumber, String message, BigDecimal amount) {
        super(source);
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.amount = amount;
    }

}
