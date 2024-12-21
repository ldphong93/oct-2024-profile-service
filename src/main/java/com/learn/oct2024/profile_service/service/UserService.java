package com.learn.oct2024.profile_service.service;

import com.learn.oct2024.common.model.dto.UserActionRequest;
import com.learn.oct2024.common.model.dto.UserInfoRequest;
import com.learn.oct2024.common.model.entity.AppUser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<AppUser> createUser(UserInfoRequest userInfoRequest);

    void handleUserAction(ConsumerRecord<String, UserActionRequest> record);

    void getUserInfo(UserActionRequest userAction, Headers headers);

    void adjustBalance(UserActionRequest userAction, Headers headers);
}
