package tech.zetapioneers.loan_application.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanStatus {
    @NotBlank
    private String status; // APPROVED or REJECTED
    private String reviewRemarks;
}
