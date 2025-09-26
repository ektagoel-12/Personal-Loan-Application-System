package tech.zetapioneers.loan_application.concreteservice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;
import tech.zetapioneers.loan_application.dto.AdminStats;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.enums.Status;
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

    @Override
    public AdminDto getAdminDashboard() {
        return null;
    }

    @Override
    public AdminLoansList updateStatus(Long id, String status, String remarks) {
        return null;
    }
//
//    @Override
//    public AdminDto getAdminDashboard() {
//        List<LoanApplication> allApps = loanRepo.findAll();
//
//        long total = allApps.size();
//        long approved = allApps.stream().filter(a -> a.getStatus() == Status.APPROVED).count();
//        long pending = allApps.stream().filter(a -> a.getStatus() == Status.PENDING).count();
//        double avgIncome = allApps.stream().mapToDouble(LoanApplication::getIncome).average().orElse(0);
//
//        Map<String, Long> statusDist = allApps.stream()
//                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));
//
//        Map<String, Long> monthlyCounts = allApps.stream()
//                .collect(Collectors.groupingBy(
//                        app -> app.getApplicationDate().getMonth().name() + " " + app.getApplicationDate().getYear(),
//                        Collectors.counting()
//                ));
//
//        List<Map<String, Long>> monthlyTrends = new ArrayList<>();
//        monthlyTrends.add(monthlyCounts);
//
//        AdminStats stats = AdminStats.builder()
//                .totalApplications(total)
//                .approvalRate(total > 0 ? (approved * 100.0 / total) : 0)
//                .pending(pending)
//                .avgIncome(avgIncome)
//                .statusDistribution(statusDist)
//                .monthlyTrends(monthlyTrends)
//                .build();
//
//        List<AdminLoansList> pendingApps = loanRepo.findByStatus(Status.PENDING)
//                .stream().map(this::mapToDTO).collect(Collectors.toList());
//
//        return AdminDto.builder()
//                .stats(stats)
//                .pendingApplications(pendingApps)
//                .build();
//    }
//
//    @Override
//    public AdminLoansList updateStatus(Long id, String status, String remarks) {
//        LoanApplication app = loanRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Loan not found"));
//
//        app.setStatus(Status.valueOf(status));
//        app.setReviewedAt(LocalDateTime.now());
//        app.setReviewRemarks(remarks);
//
//        loanRepo.save(app);
//        return mapToDTO(app);
//    }
//
//    private AdminLoansList mapToDTO(LoanApplication app) {
//        return AdminLoansList.builder()
//                .id(app.getId())
//                .name(app.getName())
//                .amount(app.getAmount())
//                .income(app.getIncome())
//                .creditScore(app.getCreditScore())
//                .type(app.getType())
//                .applicationDate(app.getApplicationDate())
//                .status(app.getStatus().name())
//                .build();
//    }
}
