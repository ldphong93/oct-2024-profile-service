package com.learn.oct2024.profile_service.controller;

import com.learn.oct2024.common.model.dto.UserInfoRequest;
import com.learn.oct2024.common.model.entity.AppUser;
import com.learn.oct2024.profile_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j(topic = "ProfileController")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('bettor')")
    public String helloUser() {
        return "Hello User";
    }

    @PostMapping(value = "/user/create")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<AppUser> createUser(@RequestBody UserInfoRequest userInfoRequest) {
        return userService.createUser(userInfoRequest);
    }
}
