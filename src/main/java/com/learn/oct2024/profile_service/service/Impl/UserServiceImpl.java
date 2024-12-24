package com.learn.oct2024.profile_service.service.Impl;

import com.learn.oct2024.common.model.dto.UserActionRequest;
import com.learn.oct2024.common.model.dto.UserInfoRequest;
import com.learn.oct2024.common.model.entity.AppUser;
import com.learn.oct2024.profile_service.repository.UserRepository;
import com.learn.oct2024.profile_service.service.KafkaService;
import com.learn.oct2024.profile_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "ProfileServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public ResponseEntity<AppUser> createUser(UserInfoRequest request) {
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));
        AppUser newUser = AppUser.builder()
                .username(request.getUsername())
                .passwordHint(request.getPassword())
                .password(hashedPassword)
                .role(request.getRole())
                .balance(request.getBalance())
                .build();
        AppUser savedUser = userRepository.save(newUser);

        savedUser.setPassword(null);
        log.info("AppUser created successfully: " + savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @Override
    @KafkaListener(topics = "request-topic", groupId = "group-id", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserAction(ConsumerRecord<String, UserActionRequest> record) {
        UserActionRequest userAction = record.value();

        switch (userAction.getAction()) {
            case GET_INFO:
                log.info("Receive request user info: " + userAction);
                this.getUserInfo(userAction, record.headers());
                break;
            case ADJUST_BALANCE:
                log.info("Receive request to adjust balance: " + userAction);
                this.adjustBalance(userAction, record.headers());
                break;
            default:
                break;
        }
    }

    @Override
    public void getUserInfo(UserActionRequest userAction, Headers headers) {
        String correlationId = new String(headers.lastHeader(KafkaHeaders.CORRELATION_ID).value(), StandardCharsets.UTF_8);
        log.info("Received request with correlationId: {}, userId:  {}", correlationId, userAction.getId());

        Optional<AppUser> user = userRepository.findById(userAction.getId());
        kafkaService.responseUserAction(user, headers);
    }

    @Override
    public void adjustBalance(UserActionRequest userAction, Headers headers) {
        String correlationId = new String(headers.lastHeader(KafkaHeaders.CORRELATION_ID).value(), StandardCharsets.UTF_8);
        log.info("Received adjust balance request with correlationId: {}, userId:  {}", correlationId, userAction.getId());

        Optional<AppUser> optionalAppUser = userRepository.findById(userAction.getId());
        if (optionalAppUser.isEmpty()) {
            log.info("Bettor not found", userAction.getId());
        }

        AppUser appUser = optionalAppUser.get();
        //Update bettor's balance
        appUser.setBalance(appUser.getBalance() + userAction.getAmount());
        userRepository.save(appUser);

        //Update booker balance
        List<AppUser> bookerList = userRepository.findByRole("booker");
        if (bookerList.isEmpty()) {
            log.info("No booker available");
        }
        AppUser firstBooker = bookerList.get(0);
        firstBooker.setBalance(firstBooker.getBalance() - userAction.getAmount());
        userRepository.save(firstBooker);
    }

    @Override
    public ResponseEntity<AppUser> getUserById(String userId) {
        log.info("Receive get user info by id request, with id: " + userId);
        Optional<AppUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        } else {
            return ResponseEntity.ok(AppUser.builder().build());
        }
    }
}
