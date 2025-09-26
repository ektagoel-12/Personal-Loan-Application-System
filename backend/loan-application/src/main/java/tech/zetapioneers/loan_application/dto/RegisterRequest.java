package tech.zetapioneers.loan_application.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class RegisterRequest implements Serializable {
    String name;
    String email;
    Double income;
    Integer creditScore;
    String password;
}
