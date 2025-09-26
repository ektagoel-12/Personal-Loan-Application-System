package tech.zetapioneers.loan_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLoansList {
    private Long id;
    private Double amount;
    private Integer tenureMonths;
    private Double income;
    private Integer creditScore;
    private String status;
    private String purpose;
    private LocalDate reviewedAt;
    private Long reviewedBy;
    private String reviewRemarks;
}
