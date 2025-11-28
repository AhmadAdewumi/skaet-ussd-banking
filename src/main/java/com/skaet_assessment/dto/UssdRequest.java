package com.skaet_assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdRequest {
    private String sessionId; //-- unique id stored in redis
    private String serviceCode; //-- code dialed e.g *123#
    private String phoneNumber;
    private String text; //-- the input string e.g 1*5*500

    //-- helper to get the last input only (ignoring the history chain)
    public String getUserInput() {
        if (text == null || text.isEmpty()) return "";

        String[] parts = text.split("\\*");

        return parts[parts.length - 1];
    }
}