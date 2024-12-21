package com.learn.oct2024.profile_service.service.Impl;

import com.learn.oct2024.common.model.dto.UserInfoResponse;
import com.learn.oct2024.common.model.entity.AppUser;
import com.learn.oct2024.profile_service.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j(topic = "KafkaServiceImpl")
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, UserInfoResponse> replyKafkaTemplate;

    @Override
    public void responseUserAction(Optional<AppUser> optionalUser, Headers headers) {
        UserInfoResponse userInfoResponse;
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            userInfoResponse = UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .balance(user.getBalance())
                    .build();
        } else {
            userInfoResponse = new UserInfoResponse();
        }

        String correlationId = new String(headers.lastHeader(KafkaHeaders.CORRELATION_ID).value(), StandardCharsets.UTF_8);
        Message<UserInfoResponse> message = MessageBuilder.withPayload(userInfoResponse)
                .setHeader(KafkaHeaders.TOPIC, "response-topic")
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .build();

        CompletableFuture<SendResult<String, UserInfoResponse>> future = replyKafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send response for correlationId: {}", correlationId, ex);
            } else {
                log.info("Sent response successfully for correlationId: {}, UserInfoResponse: {}", correlationId, userInfoResponse);
            }
        });
    }
}
