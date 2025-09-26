package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;

public abstract class AdminLoanService {
    public abstract AdminDto getAdminDashboard();
    public abstract AdminLoansList updateStatus(Long id, String status, String remarks);
}
