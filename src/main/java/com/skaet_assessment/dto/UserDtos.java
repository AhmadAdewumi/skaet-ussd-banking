package com.skaet_assessment.dto;

import lombok.Builder;
import lombok.Data;

public class UserDtos {
    @Data
    @Builder
    public static class RegistrationRequest {
        private String phoneNumber;
        private String name;
        private String pin;
    }

    @Data
    @Builder
    public static class UserResponse {
        private String phoneNumber;
        private String name;
        private boolean active;
    }
}