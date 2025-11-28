package com.skaet_assessment.soapIntegration;

import com.skaet_assessment.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * The Client/Adapter between our domain logic and the external system we are interacting with (just pretending to be the client actuallly  )
 */
@Service
@Slf4j
public class SoapExchangeRateAdapter implements ExchangeRateService {

    @Override
    @Cacheable("exchangeRates") //-- caching the exchange rate to avoid querying every time, and I learnt SOAP calls are expensive and slow
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        log.info("[SOAP ADAPTER] Preparing SOAP Envelope...");

        SoapPayloads.GetConversionRateRequest request = new SoapPayloads.GetConversionRateRequest();
        request.setFromCurrency(fromCurrency);
        request.setToCurrency(toCurrency);

        log.info("[SOAP ADAPTER] Payload: <GetConversionRateRequest><From>{}</From><To>{}</To>...</GetConversionRateRequest>",
                request.getFromCurrency(), request.getToCurrency());

        try {
            //-- simulating a network call
            Thread.sleep(500);

            //-- manually creating the object that will be created with SOAP unmarshaller if we are interacting with a real server
            SoapPayloads.GetConversionRateResponse response = new SoapPayloads.GetConversionRateResponse();
            response.setRate(getMockRate(fromCurrency, toCurrency));
            response.setTimestamp(java.time.LocalDateTime.now().toString());

            log.info("<< [SOAP ADAPTER] Received Response: <Rate>{}</Rate>", response.getRate());

            return response.getRate();

        } catch (Exception e) {
            log.error("SOAP Failure", e);
            return BigDecimal.ZERO; //-- fallback if the server fails instead of crashing the USSD session
        }
    }

    //-- mocking an exchange rate table, and I am  handling conversion from NGN only
    private BigDecimal getMockRate(String from, String to) {
        if (!from.equals("NGN")) return BigDecimal.ONE;

        return switch (to) {
            case "USD" -> new BigDecimal("0.00066");
            case "EUR" -> new BigDecimal("0.00060");
            case "GBP" -> new BigDecimal("0.00052");
            case "CAD" -> new BigDecimal("0.00090");
            default -> BigDecimal.ONE;
        };
    }
}