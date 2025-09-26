package tech.zetapioneers.loan_application.controllers;

import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.concreteservice.LoanApplicationServiceImpl;
import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.entities.LoanApplication;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    LoanApplicationServiceImpl loanApplicationServiceImpl;

    public LoanApplicationController(LoanApplicationServiceImpl loanApplicationServiceImpl) {
        this.loanApplicationServiceImpl = loanApplicationServiceImpl;
    }

    @GetMapping
    public List<LoanApplicationResponse> getAllLoans(){
        return loanApplicationServiceImpl.getAllLoans();
    }


    // Get loan by id
    @GetMapping("/{id}")
    public LoanApplicationResponse getLoanById(@PathVariable Long id) {
        return loanApplicationServiceImpl.getLoanById(id);
    }

    // Get all loans by user id
    @GetMapping("/user/{userId}")
    public List<LoanApplicationResponse> getLoansByUserId(@PathVariable Long userId) {
        return loanApplicationServiceImpl.getLoanByUser(userId);
    }

    // Create new loan
    @PostMapping
    public LoanApplicationResponse createLoan(@RequestBody LoanApplicationResponse loan) {
        System.out.println(loan.getAmount());
        Long id = loanApplicationServiceImpl.addLoanApplication(loan);
        loan.setId(id);
        loan.setRateOfInterest(loan.getPurpose().getInterestRate());
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

