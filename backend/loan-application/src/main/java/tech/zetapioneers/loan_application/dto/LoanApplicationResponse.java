package tech.zetapioneers.loan_application.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zetapioneers.loan_application.enums.LoanType;
import tech.zetapioneers.loan_application.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplicationResponse {
    private Long id;
    private Long userId;
    private String name;
    private Integer creditScore;
    private Double income;
    private Double amount;
    private LoanType purpose;
    private Status status;
    private LocalDate applicationDate;
    private LocalDateTime lastUpdated;
    private Double rateOfInterest;
    private Integer tenure;
    private String remarks;
    private String remarksBy;
}
