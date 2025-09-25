package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.dto.AuthResponse;
import tech.zetapioneers.loan_application.entities.User;

public interface AuthService {
    AuthResponse register(User user);
    AuthResponse login(String email,String password);
}
