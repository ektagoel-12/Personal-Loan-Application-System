package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.DTO.AuthResponse;
import tech.zetapioneers.loan_application.Entities.User;

public interface AuthService {
    AuthResponse register(User user);
    AuthResponse login(String email,String password);
}
