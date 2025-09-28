package tech.zetapioneers.loan_application.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;
import tech.zetapioneers.loan_application.dto.UpdateLoanStatus;
import tech.zetapioneers.loan_application.enums.Status;
import tech.zetapioneers.loan_application.services.AdminLoanService;
import tech.zetapioneers.loan_application.services.RepaymentScheduleService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminLoanService loanService;
    private final RepaymentScheduleService repaymentScheduleService;

    @GetMapping
    public AdminDto getAdminDashboard() {
        return loanService.getAdminDashboard();
    }

    @GetMapping("/loans/{id}")
    public AdminLoansList getLoan(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    @PutMapping("/loans/{id}/status")
    public AdminLoansList updateStatus(@PathVariable Long id, @RequestBody UpdateLoanStatus req) {
        AdminLoansList result = loanService.updateStatus(id, req.getStatus(), req.getReviewRemarks(), req.getReviewedAt(), req.getReviewedBy());
        if(Status.valueOf(result.getStatus()) == Status.APPROVED)
            repaymentScheduleService.generateSchedule(id);
        return result;
    }
}
