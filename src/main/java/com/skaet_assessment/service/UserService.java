package com.skaet_assessment.service;

import com.skaet_assessment.dto.UserDtos;
import com.skaet_assessment.mapper.EntityMapper;
import com.skaet_assessment.model.User;
import com.skaet_assessment.model.Wallet;
import com.skaet_assessment.repository.UserRepository;
import com.skaet_assessment.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final EntityMapper entityMapper;

    public UserService(UserRepository userRepository, WalletRepository walletRepository, EntityMapper entityMapper) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.entityMapper = entityMapper;
    }


    //-- registers a new user and creates default wallet for them
    @Transactional
    public void registerUser(UserDtos.RegistrationRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .name(request.getName())
                .pinHash(hashPin(request.getPin()))
                .active(true)
                .build();
        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .currencyCode("NGN")
                .build();
        walletRepository.save(wallet);

        log.info("Registered new user: {}", request.getPhoneNumber());

//        entityMapper.toUserResponse(savedUser);
    }

    public UserDtos.UserResponse login(String phoneNumber, String rawPin) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        if (!user.getPinHash().equals(hashPin(rawPin))) {
            return null;
        }

        return entityMapper.toUserResponse(user);
    }

    //-- used by other services also to fetch user
    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    //-- simple SHA-256 Hashing, Spring security will introduce complexity intto this simple app
    private String hashPin(String rawPin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPin.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing PIN", e);
        }
    }
}