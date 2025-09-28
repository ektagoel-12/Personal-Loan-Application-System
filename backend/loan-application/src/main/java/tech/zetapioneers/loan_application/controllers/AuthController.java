package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.concreteservice.JwtServiceImpl;
import tech.zetapioneers.loan_application.concreteservice.UserServiceImpl;
import tech.zetapioneers.loan_application.dto.*;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.concreteservice.AuthServiceImpl;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    JwtServiceImpl jwtService;

    @GetMapping
    public String health(){
        return "Server is up and running";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest body){
        User user = new User();
        user.setName(body.getName());
        user.setEmail(body.getEmail());
        user.setIncome(body.getIncome());
        user.setCreditScore(body.getCreditScore());
        user.setPassword(body.getPassword());
        user.setAadhar(body.getAadhar());
        return new ResponseEntity<>(authService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest body){
        return new ResponseEntity<>(authService.login(body.getEmail(), body.getPassword()),HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        try {
            Optional<Map<String, String>> claims = jwtService.verifyRefreshToken(refreshToken);
            if (claims.isEmpty()) {
                return ResponseEntity.status(401).body("Invalid or expired refresh token");
            }

            String userId = claims.get().get("sub");
            Optional<User> userOpt = userService.getUserById(Long.parseLong(userId));
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOpt.get();
            String newAccessToken = jwtService.generateAccessToken(user);
            return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error",e.getMessage()));
        }
    }

}
