package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;

import java.time.LocalDateTime;

public abstract class AdminLoanService {
    public abstract AdminDto getAdminDashboard();
    public abstract AdminLoansList updateStatus(Long id, String status, String reviewRemarks, LocalDateTime reviewedAt, String reviewedBy);
    public abstract AdminLoansList getLoanById(Long id);
}
