package com.jake.messagesystem.controller;

import com.jake.messagesystem.dto.rest.req.UserRegisterRequest;
import com.jake.messagesystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class MessageUserController {
    private static final Logger log = LoggerFactory.getLogger(MessageUserController.class);

    private final UserService userService;

    public MessageUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
        try {
            userService.addUser(request.username(), request.password());

            return ResponseEntity.ok("User registered.");
        } catch (Exception e) {
            log.error("Add user failed. cause: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Register user failed.");
        }
    }

    @PostMapping("/unregister")
    public ResponseEntity<String> unregister(HttpServletRequest request) {
        try {
            userService.removeUser();
            request.getSession().invalidate();

            return ResponseEntity.ok("User unregistered.");
        } catch (Exception e) {
            log.error("Remove user failed. cause: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Remove user failed.");
        }
    }
}
