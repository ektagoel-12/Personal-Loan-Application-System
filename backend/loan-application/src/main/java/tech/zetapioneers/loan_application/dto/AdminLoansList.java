package tech.zetapioneers.loan_application.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zetapioneers.loan_application.enums.LoanType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLoansList {
    private Long id;
    private String name;
    private Double amount;
    private Double income;
    private Integer creditScore;
    private LoanType type;
    private LocalDate applicationDate;
    private String status;

    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewRemarks;
}
