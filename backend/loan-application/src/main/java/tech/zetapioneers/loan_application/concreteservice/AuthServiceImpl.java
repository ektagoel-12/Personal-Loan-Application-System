package tech.zetapioneers.loan_application.concreteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.AuthResponse;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.services.AuthService;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServiceImpl jwtService;

    @Override
    public AuthResponse register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRole() == null){
            user.setRole(Role.USER);
        }

        user.setCreatedAt(Date.valueOf(LocalDate.now()));
        user.setUpdatedAt(Date.valueOf(LocalDate.now()));

        User newUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setName(newUser.getName());
        response.setRole(newUser.getRole().name());
        response.setIncome(newUser.getIncome());
        response.setEmail(newUser.getEmail());
        response.setCreditScore(newUser.getCreditScore());
        return response;
    }

    @Override
    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setIncome(user.getIncome());
        response.setEmail(user.getEmail());
        response.setCreditScore(user.getCreditScore());
        return response;
    }

}
