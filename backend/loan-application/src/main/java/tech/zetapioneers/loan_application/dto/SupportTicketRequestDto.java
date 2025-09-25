package tech.zetapioneers.loan_application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class SupportTicketRequestDto {
    private String userEmail;
    private Long loanId;
    private String subject;
    private String description;
}
