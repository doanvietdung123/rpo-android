package com.spring.backend.controllers;

import com.spring.backend.models.User;
import com.spring.backend.repositories.UserRepository;
import com.spring.backend.tools.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String login = credentials.get("login");
        String pwd = credentials.get("password");
        if (!pwd.isEmpty() && !login.isEmpty()) {
            Optional<User> uu = userRepository.findByLogin(login);
            if (uu.isPresent()) {
                User u2 = uu.get();
                String hash1 = u2.getPassword();
                String salt = u2.getSalt();
                String hash2 = Utils.ComputeHash(pwd, salt);
                if (hash1.equalsIgnoreCase(hash2)) {
                    String token = UUID.randomUUID().toString();
                    u2.setToken(token);
                    u2.setActivity(LocalDateTime.now());
                    User u3 = userRepository.saveAndFlush(u2);
                    return new ResponseEntity<Object>(u3, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            token = StringUtils.removeStart(token, "Bearer").trim();
            Optional<User> uu = userRepository.findByToken(token);
            if (uu.isPresent()) {
                User u = uu.get();
                u.setToken(null);
                userRepository.save(u);
                return new ResponseEntity<Object>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
    }

}