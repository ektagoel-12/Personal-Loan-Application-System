package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.enums.LoanType;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanApplicationServiceImplTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    private User user;
    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        loanApplication = new LoanApplication();
        loanApplication.setId(100L);
        loanApplication.setUser(user);
        loanApplication.setAmount(50000.0);
        loanApplication.setTenureMonths(12);
        loanApplication.setIncome(40000.0);
        loanApplication.setCreditScore(750);
        loanApplication.setStatus(LoanStatus.PENDING);
        loanApplication.setApplicationDate(LocalDate.of(2025, 9, 25));
        loanApplication.setLoanType(LoanType.CAR_LOAN); //  FIXED: prevent NPE
    }

    @Test
    void addLoanApplication() {
        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setUserId(user.getId());
        response.setAmount(loanApplication.getAmount());
        response.setTenure(loanApplication.getTenureMonths());
        response.setIncome(loanApplication.getIncome());
        response.setCreditScore(loanApplication.getCreditScore());
        response.setStatus(LoanStatus.PENDING);
        response.setApplicationDate(loanApplication.getApplicationDate());
        response.setPurpose(loanApplication.getLoanType());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        Long loanId = loanApplicationService.addLoanApplication(response);

        assertEquals(loanApplication.getId(), loanId);
        verify(userRepository, times(1)).findById(user.getId());
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void getAllLoans() {
        when(loanApplicationRepository.findAll()).thenReturn(Arrays.asList(loanApplication));

        List<LoanApplicationResponse> result = loanApplicationService.getAllLoans();

        assertEquals(1, result.size());
        assertEquals(loanApplication.getId(), result.get(0).getId());
        assertEquals(user.getName(), result.get(0).getName());
        assertEquals(LoanType.CAR_LOAN, result.get(0).getPurpose());
        verify(loanApplicationRepository, times(1)).findAll();
    }

    @Test
    void getLoanByUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findAllByUser(user)).thenReturn(Arrays.asList(loanApplication));

        List<LoanApplicationResponse> result = loanApplicationService.getLoanByUser(user.getId());

        assertEquals(1, result.size());
        assertEquals(loanApplication.getId(), result.get(0).getId());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(LoanType.CAR_LOAN, result.get(0).getPurpose());
        verify(userRepository, times(1)).findById(user.getId());
        verify(loanApplicationRepository, times(1)).findAllByUser(user);
    }

    @Test
    void getLoanById() {
        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        LoanApplicationResponse result = loanApplicationService.getLoanById(loanApplication.getId());

        assertNotNull(result);
        assertEquals(loanApplication.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(LoanType.CAR_LOAN, result.getPurpose());
        verify(loanApplicationRepository, times(1)).findById(loanApplication.getId());
    }
}
