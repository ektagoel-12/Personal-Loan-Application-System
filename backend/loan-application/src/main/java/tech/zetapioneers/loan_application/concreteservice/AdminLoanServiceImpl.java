package tech.zetapioneers.loan_application.concreteservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;
import tech.zetapioneers.loan_application.dto.AdminStats;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.services.AdminLoanService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLoanServiceImpl extends AdminLoanService {

    private final LoanApplicationRepository loanRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AdminDto getAdminDashboard() {
        List<LoanApplication> allApps = loanRepo.findAll();

        long total = allApps.size();
        long approved = allApps.stream().filter(a -> a.getStatus() == LoanStatus.APPROVED).count();
        long pending = allApps.stream().filter(a -> a.getStatus() == LoanStatus.PENDING).count();
        double avgIncome = allApps.stream().mapToDouble(LoanApplication::getIncome).average().orElse(0);

        Map<String, Long> statusDist = allApps.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));

        Map<String, Long> monthlyCounts = allApps.stream()
                .filter(app -> app.getApplicationDate() != null)
                .collect(Collectors.groupingBy(
                        app -> app.getApplicationDate().getMonth().name().substring(0,3),
                        Collectors.counting()
                ));

        AdminStats stats = AdminStats.builder()
                .totalApplications(total)
                .approvalRate(total > 0 ? (approved * 100.0 / total) : 0)
                .pending(pending)
                .avgIncome(avgIncome)
                .statusDistribution(statusDist)
                .monthlyCounts(monthlyCounts)
                .build();

        List<AdminLoansList> pendingApps = loanRepo.findByStatus(LoanStatus.PENDING)
                .stream().map(this::mapToDTO).collect(Collectors.toList());

        return AdminDto.builder()
                .stats(stats)
                .pendingApplications(pendingApps)
                .build();
    }

    @Override
    public AdminLoansList updateStatus(Long id, String status, String reviewRemarks, LocalDateTime reviewedAt, String reviewedBy) {
        LoanApplication app = loanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        app.setStatus(LoanStatus.valueOf(status));
        app.setReviewedBy(reviewedBy);
        app.setReviewedAt(reviewedAt);
        app.setReviewRemarks(reviewRemarks);


        loanRepo.save(app);
        return mapToDTO(app);
    }

    @Override
    public AdminLoansList getLoanById(Long id) {
        LoanApplication app = loanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        return mapToDTO(app);
    }

    private AdminLoansList mapToDTO(LoanApplication app) {
        return AdminLoansList.builder()
                .id(app.getId())
                .name(app.getUser().getName())
                .amount(app.getAmount())
                .income(app.getIncome())
                .creditScore(app.getCreditScore())
                .loanType(app.getLoanType())
                .applicationDate(app.getApplicationDate())
                .status(app.getStatus().name())
                .reviewedAt(app.getReviewedAt())
                .reviewedBy(app.getReviewedBy())
                .reviewRemarks(app.getReviewRemarks())
                .build();
    }
}
