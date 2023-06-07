package com.spring.backend.controllers;

import com.spring.backend.models.Museum;
import com.spring.backend.models.User;
import com.spring.backend.repositories.MuseumRepository;
import com.spring.backend.repositories.UserRepository;
import com.spring.backend.tools.DataValidationException;
import com.spring.backend.tools.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MuseumRepository museumRepository;
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User requestUser) throws Exception{
        try {
            User user = userRepository.save(requestUser);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("user.login_UNIQUE"))
                error = "user already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable(value = "id") Long userId,
                                     @Valid @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.setEmail(userDetails.getEmail());
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.setPassword(Utils.ComputeHash(np, salt));
                user.setSalt(salt);
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.email_UNIQUE"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/addMuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                             @Validated @RequestBody Set<Museum> museums) {
        Optional<User> userOptional = userRepository.findById(userId);
        int cnt = 0;
        if (userOptional.isPresent()) {
            User u = userOptional.get();
            for (Museum m : museums) {
                Optional<Museum> museumOptional = museumRepository.findById(m.getId());
                if (museumOptional.isPresent()) {
                    u.addMuseum(museumOptional.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        int cnt = 0;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (Museum museum : user.museums) {
                user.removeMuseum(museum);
                cnt++;
            }
            userRepository.save(user);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }
}
