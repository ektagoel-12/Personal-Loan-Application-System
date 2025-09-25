package tech.zetapioneers.loan_application.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLoanStatus {
    @NotBlank
    private String status; // APPROVED or REJECTED
    private String reviewRemarks;
}
