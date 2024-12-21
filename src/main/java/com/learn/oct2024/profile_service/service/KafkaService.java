package com.learn.oct2024.profile_service.service;

import com.learn.oct2024.common.model.entity.AppUser;
import org.apache.kafka.common.header.Headers;

import java.util.Optional;

public interface KafkaService {

    void responseUserAction(Optional<AppUser> optionalUser, Headers headers);
}
