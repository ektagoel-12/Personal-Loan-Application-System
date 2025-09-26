package tech.zetapioneers.loan_application.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String name;
    private String email;
    private String password;
    private Integer creditScore;
    private Double income;
    private Role role;
    public void applyUpdates(User user) {
        if (this.name != null) user.setName(this.name);
        if (this.email != null) user.setEmail(this.email);
        if (this.creditScore != null) user.setCreditScore(this.creditScore);
        if (this.income != null) user.setIncome(this.income);
        if (this.role != null) user.setRole(this.role);
        if (this.password != null) {
            user.setPassword(passwordEncoder.encode(this.password));
        }
        user.setUpdatedAt(Date.valueOf(LocalDate.now()));
    }
}
