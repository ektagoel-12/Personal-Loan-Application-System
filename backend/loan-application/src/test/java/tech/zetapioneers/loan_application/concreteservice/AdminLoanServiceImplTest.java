package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zetapioneers.loan_application.dto.AdminDto;
import tech.zetapioneers.loan_application.dto.AdminLoansList;
import tech.zetapioneers.loan_application.dto.AdminStats;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.enums.LoanType;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static tech.zetapioneers.loan_application.enums.LoanType.*;

@ExtendWith(MockitoExtension.class)

class AdminLoanServiceImplTest {

    @Mock
    private LoanApplicationRepository loanRepo;

    @InjectMocks
    private AdminLoanServiceImpl adminLoanService;

    private LoanApplication loanApplication1;
    private LoanApplication loanApplication2;
    private LoanApplication loanApplication3;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");

        user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");

        loanApplication1 = new LoanApplication();
        loanApplication1.setId(1L);
        loanApplication1.setUser(user1);
        loanApplication1.setAmount(50000.0);
        loanApplication1.setIncome(80000.0);
        loanApplication1.setCreditScore(750);
        loanApplication1.setLoanType(HOME_LOAN);
        loanApplication1.setStatus(LoanStatus.PENDING);
        loanApplication1.setApplicationDate(LocalDate.of(2024, Month.JANUARY, 15));

        loanApplication2 = new LoanApplication();
        loanApplication2.setId(2L);
        loanApplication2.setUser(user2);
        loanApplication2.setAmount(25000.0);
        loanApplication2.setIncome(60000.0);
        loanApplication2.setCreditScore(680);
        loanApplication2.setLoanType(PERSONAL_LOAN);
        loanApplication2.setStatus(LoanStatus.APPROVED);
        loanApplication2.setApplicationDate(LocalDate.of(2024, Month.FEBRUARY, 20));

        loanApplication3 = new LoanApplication();
        loanApplication3.setId(3L);
        loanApplication3.setUser(user1);
        loanApplication3.setAmount(15000.0);
        loanApplication3.setIncome(45000.0);
        loanApplication3.setCreditScore(600);
        loanApplication3.setLoanType(EDUCATION_LOAN);
        loanApplication3.setStatus(LoanStatus.REJECTED);
        loanApplication3.setApplicationDate(LocalDate.of(2024, Month.JANUARY, 25));
    }


    @Test
    void getAdminDashboard() {
        List<LoanApplication> allApplications = Arrays.asList(loanApplication1, loanApplication2, loanApplication3);
        List<LoanApplication> pendingApplications = Collections.singletonList(loanApplication1);

        when(loanRepo.findAll()).thenReturn(allApplications);
        when(loanRepo.findByStatus(LoanStatus.PENDING)).thenReturn(pendingApplications);

        // When
        AdminDto result = adminLoanService.getAdminDashboard();

        // Then
        assertNotNull(result);

        // Verify stats
        AdminStats stats = result.getStats();
        assertEquals(3, stats.getTotalApplications());
        assertEquals(33.33, stats.getApprovalRate(), 0.01); // 1 approved out of 3
        assertEquals(1, stats.getPending());
        assertEquals(61666.67, stats.getAvgIncome(), 0.01); // (80000 + 60000 + 45000) / 3

        // Verify status distribution
        assertEquals(1L, stats.getStatusDistribution().get("PENDING"));
        assertEquals(1L, stats.getStatusDistribution().get("APPROVED"));
        assertEquals(1L, stats.getStatusDistribution().get("REJECTED"));

        // Verify monthly counts
        assertEquals(2L, stats.getMonthlyCounts().get("JAN")); // 2 applications in January
        assertEquals(1L, stats.getMonthlyCounts().get("FEB")); // 1 application in February

        // Verify pending applications
        assertEquals(1, result.getPendingApplications().size());
        AdminLoansList pendingApp = result.getPendingApplications().get(0);
        assertEquals(1L, pendingApp.getId());
        assertEquals("John Doe", pendingApp.getName());
        assertEquals("PENDING", pendingApp.getStatus());

        // Verify repository calls
        verify(loanRepo).findAll();
        verify(loanRepo).findByStatus(LoanStatus.PENDING);
    }

    @Test
    void updateStatus() {
        Long loanId = 1L;
        String newStatus = "APPROVED";
        String reviewRemarks = "Approved after verification";
        LocalDateTime reviewedAt = LocalDateTime.now();
        String reviewedBy = "admin1";

        when(loanRepo.findById(loanId)).thenReturn(Optional.of(loanApplication1));
        when(loanRepo.save(any(LoanApplication.class))).thenReturn(loanApplication1);

        // When
        AdminLoansList result = adminLoanService.updateStatus(loanId, newStatus, reviewRemarks, reviewedAt, reviewedBy);

        // Then
        assertNotNull(result);
        assertEquals(loanId, result.getId());
        assertEquals("APPROVED", result.getStatus());
        assertEquals("John Doe", result.getName());
        assertEquals(50000.0, result.getAmount());
        assertEquals(80000.0, result.getIncome());
        assertEquals(750, result.getCreditScore());
        assertEquals(LoanType.HOME_LOAN, result.getLoanType());

        // Verify that the loan application was updated
        verify(loanRepo).findById(loanId);
        verify(loanRepo).save(loanApplication1);
        assertEquals(LoanStatus.APPROVED, loanApplication1.getStatus());
        assertEquals(reviewRemarks, loanApplication1.getReviewRemarks());
        assertEquals(reviewedAt, loanApplication1.getReviewedAt());
        assertEquals(reviewedBy, loanApplication1.getReviewedBy());

        // Test exception case
        when(loanRepo.findById(999L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminLoanService.updateStatus(999L, "APPROVED", "remarks", LocalDateTime.now(), "admin1");
        });
        assertEquals("Loan not found", exception.getMessage());
    }

    @Test
    void getLoanById() {
        Long loanId = 1L;
        when(loanRepo.findById(loanId)).thenReturn(Optional.of(loanApplication1));

        // When
        AdminLoansList result = adminLoanService.getLoanById(loanId);

        // Then
        assertNotNull(result);
        assertEquals(loanId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals(50000.0, result.getAmount());
        assertEquals(80000.0, result.getIncome());
        assertEquals(750, result.getCreditScore());
        assertEquals(LoanType.HOME_LOAN, result.getLoanType());
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getApplicationDate());

        // Verify repository call
        verify(loanRepo).findById(loanId);

        // Test exception case
        when(loanRepo.findById(999L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminLoanService.getLoanById(999L);
        });
        assertEquals("Loan not found", exception.getMessage());
    }
}