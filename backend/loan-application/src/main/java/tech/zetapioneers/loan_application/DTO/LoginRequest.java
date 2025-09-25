package tech.zetapioneers.loan_application.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest  implements Serializable {
    String email;
    String password;
}
