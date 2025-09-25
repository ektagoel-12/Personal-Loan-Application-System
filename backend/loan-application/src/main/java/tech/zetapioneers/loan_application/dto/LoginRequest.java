package tech.zetapioneers.loan_application.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest  implements Serializable {
    String email;
    String password;
}
