package tech.zetapioneers.loan_application.controllers;


import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    public LoanApplicationController() {

    }

    // Get all loan applications
    @GetMapping
    public List<LoanApplication> getAllLoans() {
        return null;
    }

    // Get loan by id
    @GetMapping("/{id}")
    public LoanApplication getLoanById(@PathVariable Long id) {
        return null;
    }

    // Get all loans by user id
    @GetMapping("/user/{userId}")
    public List<LoanApplication> getLoansByUserId(@PathVariable Long userId) {
        return null;
    }

    // Create new loan
    @PostMapping
    public LoanApplication createLoan(@RequestBody LoanApplication loan) {
        return null;
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

