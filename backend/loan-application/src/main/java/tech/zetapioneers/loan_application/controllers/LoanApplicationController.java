package tech.zetapioneers.loan_application.controllers;


import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.concreteservice.LoanApplicationService;
import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.entities.LoanApplication;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }


    // Get loan by id
    @GetMapping("/{id}")
    public LoanApplicationResponse getLoanById(@PathVariable Long id) {
        return loanApplicationService.getLoanById(id);
    }

    // Get all loans by user id
    @GetMapping("/user/{userId}")
    public List<LoanApplicationResponse> getLoansByUserId(@PathVariable Long userId) {
        return loanApplicationService.getLoanByUser(userId);
    }

    // Create new loan
    @PostMapping
    public LoanApplicationResponse createLoan(@RequestBody LoanApplicationResponse loan) {
        System.out.println("Loan recieved");
        Long id = loanApplicationService.addLoanApplication(loan);
        loan.setId(id);
        return loan;
    }

    // Update loan
    @PutMapping("/{id}")
    public LoanApplication updateLoan(@PathVariable Long id, @RequestBody LoanApplication updatedLoan) {
        return null;
    }

    // Delete loan
    @DeleteMapping("/{id}")
    public String deleteLoan(@PathVariable Long id) {
        return null;
    }
}

