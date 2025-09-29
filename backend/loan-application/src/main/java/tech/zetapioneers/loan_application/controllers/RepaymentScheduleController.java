package tech.zetapioneers.loan_application.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.EmiPreviewRequest;
import tech.zetapioneers.loan_application.dto.EmiPreviewResponse;
import tech.zetapioneers.loan_application.dto.RepaymentScheduleDTO;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.concreteservice.RepaymentScheduleServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RepaymentScheduleController {

    private final RepaymentScheduleServiceImpl repaymentScheduleService;
    private final LoanApplicationRepository loanApplicationRepository;

    // POST /emi/preview
    @PostMapping("/emi/preview")
    public ResponseEntity<EmiPreviewResponse> previewEmi(@RequestBody EmiPreviewRequest request) {
        return ResponseEntity.ok(repaymentScheduleService.previewEmi(request));
    }

    // GET /loans/{loanId}/schedule
    @GetMapping("/loans/{loanId}/schedule")
    public ResponseEntity<List<RepaymentScheduleDTO>> getSchedule(@PathVariable Long loanId) {
        return ResponseEntity.ok(repaymentScheduleService.getSchedule(loanId));
    }

    // POST /loans/{loanId}/generate-schedule (only for APPROVED loans)
    // It may be redundant right now.
    @PostMapping("/loans/{loanId}/generate-schedule")
    public ResponseEntity<List<RepaymentSchedule>> generateSchedule(@PathVariable Long loanId) {
        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new RuntimeException("Loan is not approved. Current status: " + loan.getStatus());
        }

        // just pass the loan (rate comes from LoanType enum)
        List<RepaymentSchedule> schedule = repaymentScheduleService.generateSchedule(loan.getId());

        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/loans/{loanId}/schedule/{scheduleId}/pay")
    public ResponseEntity<String> payEmi(@PathVariable Long loanId, @PathVariable Long scheduleId) {
        repaymentScheduleService.pay(loanId, scheduleId);
        return ResponseEntity.ok("EMI with id " + scheduleId + " has been paid.");
    }
}
