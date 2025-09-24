package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.DTO.RegisterRequest;
import tech.zetapioneers.loan_application.Entities.User;
import tech.zetapioneers.loan_application.concreteservice.AuthServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthServiceImpl authService;

    @GetMapping
    public String health(){
        return "Server is up and running";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest body){
        User user = new User();
        user.setName(body.getName());
        user.setEmail(body.getEmail());
        user.setIncome(body.getIncome());
        user.setCreditScore(body.getCreditScore());
        user.setPassword(body.getPassword());
        return new ResponseEntity<>(authService.register(user), HttpStatus.CREATED);
    }
}
