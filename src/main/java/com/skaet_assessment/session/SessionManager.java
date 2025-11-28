package com.skaet_assessment.session;

import com.skaet_assessment.enums.UssdState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionManager {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.ussd.session-timeout:120}") //-- 2 seconds before the session times out
    private long timeoutSeconds; //== TTL

    public SessionManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void createSession(String sessionId, UssdState startState) {
        redisTemplate.opsForValue().set(sessionId, startState.name(), Duration.ofSeconds(timeoutSeconds));
    }

    public UssdState getCurrentState(String sessionId) {
        String state = redisTemplate.opsForValue().get(sessionId);
        return state == null ? null : UssdState.valueOf(state);
    }

    public void updateState(String sessionId, UssdState nextState) {
        redisTemplate.opsForValue().set(sessionId, nextState.name(), Duration.ofSeconds(timeoutSeconds));
    }

    public void saveTempData(String sessionId, String key, String value) {
        redisTemplate.opsForValue().set(sessionId + ":" + key, value, Duration.ofSeconds(timeoutSeconds));
    }

    public String getTempData(String sessionId, String key) {
        return redisTemplate.opsForValue().get(sessionId + ":" + key);
    }

    public void endSession(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
