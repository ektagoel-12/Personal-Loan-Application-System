package tech.zetapioneers.loan_application.services;

import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.Entities.User;

@Service
public interface JwtService {
    public String generateAccessToken(User user);
    public String generateRefreshToken(User user);
}
