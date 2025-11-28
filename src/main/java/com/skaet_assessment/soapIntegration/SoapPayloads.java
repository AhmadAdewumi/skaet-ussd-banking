package com.skaet_assessment.soapIntegration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.math.BigDecimal;

public class SoapPayloads {
    @Data
    @XmlRootElement(name = "GetConversionRateRequest")
    @XmlAccessorType(XmlAccessType.FIELD) //-- to decouple java naming convention from that of the SOAP contract
    public static class GetConversionRateRequest {
        @XmlElement(name = "FromCurrency", required = true)
        private String fromCurrency;

        @XmlElement(name = "ToCurrency", required = true)
        private String toCurrency;
    }

    @Data
    @XmlRootElement(name = "GetConversionRateResponse")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetConversionRateResponse {
        @XmlElement(name = "Rate")
        private BigDecimal rate;

        @XmlElement(name = "Timestamp")
        private String timestamp;
    }
}