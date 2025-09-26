package tech.zetapioneers.loan_application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanStatus {
    @NotBlank
    private String status;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewRemarks;
}
