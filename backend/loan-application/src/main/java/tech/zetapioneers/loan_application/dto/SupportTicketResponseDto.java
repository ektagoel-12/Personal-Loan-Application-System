package tech.zetapioneers.loan_application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zetapioneers.loan_application.enums.TicketRequestTypes;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.time.LocalDateTime;
@Data @AllArgsConstructor @NoArgsConstructor
public class SupportTicketResponseDto {
    private Long id;
    private String userEmail;
    private Long loanId;
    private String subject;
    private String description;
    private TicketRequestTypes type;
    private TicketStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
    private String response;
}
