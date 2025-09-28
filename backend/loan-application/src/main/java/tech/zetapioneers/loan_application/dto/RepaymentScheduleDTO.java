package tech.zetapioneers.loan_application.dto;

import lombok.Data;

@Data
public class RepaymentScheduleDTO {
    private Integer month;
    private Double principalAmount;
    private Double interestAmount;
    private Double balanceRemaining;
    private double emi;
    private Boolean isPaid;
    private Long id;


}
