package tech.zetapioneers.loan_application.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private String name;
    private String email;
    private String role;
    private Integer creditScore;
    private Double income;
}
