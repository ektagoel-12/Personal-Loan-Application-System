package tech.zetapioneers.loan_application.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;
import tech.zetapioneers.loan_application.services.AdminLoanService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminLoanService loanService;

    @GetMapping
    public AdminDto getAdminDashboard() {
        return loanService.getAdminDashboard();
    }

    @PutMapping("/{id}/status/{status}")
    public AdminLoansList updateStatus(
            @PathVariable Long id,
            @PathVariable String status,
            @RequestParam(required = false) String remarks) {
        return loanService.updateStatus(id, status, remarks);
    }
}
