package tech.zetapioneers.loan_application.dto;

import lombok.Data;

@Data
public class EmiPreviewResponse {
    private Double emi;
    private Double totalPayment;
    private Double totalInterest;
}
