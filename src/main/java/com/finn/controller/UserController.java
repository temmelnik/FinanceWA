package com.finn.controller;

import com.finn.domain.User;
import com.finn.domain.UserDTO;
import com.finn.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = "/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserDTO input, HttpServletRequest request) {
        input.setEmail(input.getEmail().toLowerCase());
        if(userRepository.findByEmail(input.getEmail())!=null){
            return new ResponseEntity<>("This email already registred", HttpStatus.CONFLICT);
        }

        try {
            User user= new User(input.getName(), input.getEmail(), input.getPassword());
            userRepository.save(user);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
