package tech.zetapioneers.loan_application.dto;

import lombok.Data;

@Data
public class EmiPreviewRequest {
    private Double amount;
    private Double annualInterestRate;
    private Integer tenureMonths;
}
