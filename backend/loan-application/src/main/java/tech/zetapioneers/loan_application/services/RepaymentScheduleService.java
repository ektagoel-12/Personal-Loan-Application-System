package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.dto.EmiPreviewRequest;
import tech.zetapioneers.loan_application.dto.EmiPreviewResponse;
import tech.zetapioneers.loan_application.dto.RepaymentScheduleDTO;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;

import java.util.List;

public interface RepaymentScheduleService {

    double calculateEMI(double principal, double annualRate, int months);

    List<RepaymentSchedule> generateSchedule(Long loanId);

    EmiPreviewResponse previewEmi(EmiPreviewRequest request);

    List<RepaymentScheduleDTO> getSchedule(Long loanId);

    void pay(Long loanId, Long scheduleId);
}
