package tech.zetapioneers.loan_application.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
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
