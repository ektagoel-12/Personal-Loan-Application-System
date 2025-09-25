package tech.zetapioneers.loan_application.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private String name;
    private String role;
}
