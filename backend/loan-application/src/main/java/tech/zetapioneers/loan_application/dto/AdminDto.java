package tech.zetapioneers.loan_application.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
    private AdminStats stats;
    private List<AdminLoansList> pendingApplications;
}
