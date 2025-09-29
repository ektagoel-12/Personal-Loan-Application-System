package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zetapioneers.loan_application.dto.EmiPreviewRequest;
import tech.zetapioneers.loan_application.dto.EmiPreviewResponse;
import tech.zetapioneers.loan_application.dto.RepaymentScheduleDTO;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.enums.LoanType;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.RepaymentScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentScheduleServiceImplTest {

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private RepaymentScheduleServiceImpl repaymentScheduleService;

    private LoanApplication loanApplication;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        // Create a mock LoanType enum
        loanType = mock(LoanType.class);

        loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setAmount(100000.0);
        loanApplication.setTenureMonths(12);
        loanApplication.setLoanType(loanType);
    }

    @Test
    void testCalculateEMI_StandardLoan() {
        // Given
        double principal = 100000.0;
        double annualRate = 10.0;
        int months = 12;

        // When
        double emi = repaymentScheduleService.calculateEMI(principal, annualRate, months);

        // Then
        assertNotNull(emi);
        assertTrue(emi > 0);
        // EMI formula: P * r * (1+r)^n / ((1+r)^n - 1)
        // For 100000, 10% annual (0.833% monthly), 12 months
        // Expected EMI ≈ 8791.59
        assertEquals(8791.59, emi, 1.0); // Allow 1 rupee tolerance
    }

    @Test
    void testCalculateEMI_LowInterestRate() {
        // Given
        double principal = 100000.0;
        double annualRate = 0.01; // Very low but not zero
        int months = 12;

        // When
        double emi = repaymentScheduleService.calculateEMI(principal, annualRate, months);

        // Then
        assertTrue(emi > 0);
        // With very low interest, EMI should be close to principal/months
        assertTrue(emi > principal / months);
        assertTrue(emi < (principal / months) * 1.01); // Less than 1% more
    }

    @Test
    void testCalculateEMI_ShortTenure() {
        // Given
        double principal = 50000.0;
        double annualRate = 12.0;
        int months = 6;

        // When
        double emi = repaymentScheduleService.calculateEMI(principal, annualRate, months);

        // Then
        assertTrue(emi > principal / months); // EMI should be higher than simple division
        assertTrue(emi < principal); // But less than total principal
    }

    @Test
    void testGenerateSchedule_Success() {
        // Given
        Long loanId = 1L;
        when(loanType.getInterestRate()).thenReturn(10.0); // 10% annual rate
        when(loanApplicationRepository.findById(loanId)).thenReturn(Optional.of(loanApplication));
        when(repaymentScheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<RepaymentSchedule> schedules = repaymentScheduleService.generateSchedule(loanId);

        // Then
        assertNotNull(schedules);
        assertEquals(12, schedules.size()); // Should have 12 monthly schedules

        // Verify first schedule
        RepaymentSchedule firstSchedule = schedules.get(0);
        assertEquals(1, firstSchedule.getMonth());
        assertTrue(firstSchedule.getPrincipalAmount() > 0);
        assertTrue(firstSchedule.getInterestAmount() > 0);
        assertFalse(firstSchedule.getIsPaid());
        assertEquals(loanApplication, firstSchedule.getLoan());

        // Verify last schedule has zero or near-zero balance
        RepaymentSchedule lastSchedule = schedules.get(11);
        assertEquals(12, lastSchedule.getMonth());
        assertTrue(lastSchedule.getBalanceRemaining() < 1.0); // Should be close to zero

        // Verify total principal equals loan amount
        double totalPrincipal = schedules.stream()
                .mapToDouble(RepaymentSchedule::getPrincipalAmount)
                .sum();
        assertEquals(loanApplication.getAmount(), totalPrincipal, 1.0);

        verify(loanApplicationRepository).findById(loanId);
        verify(repaymentScheduleRepository).saveAll(anyList());
    }

    @Test
    void testGenerateSchedule_BalanceDecreases() {
        // Given
        Long loanId = 1L;
        when(loanType.getInterestRate()).thenReturn(10.0);
        when(loanApplicationRepository.findById(loanId)).thenReturn(Optional.of(loanApplication));
        when(repaymentScheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<RepaymentSchedule> schedules = repaymentScheduleService.generateSchedule(loanId);

        // Then
        double previousBalance = loanApplication.getAmount();
        for (RepaymentSchedule schedule : schedules) {
            assertTrue(schedule.getBalanceRemaining() < previousBalance,
                    "Balance should decrease each month");
            previousBalance = schedule.getBalanceRemaining();
        }
    }

    @Test
    void testPreviewEmi_Success() {
        // Given
        EmiPreviewRequest request = new EmiPreviewRequest();
        request.setAmount(100000.0);
        request.setAnnualInterestRate(10.0);
        request.setTenureMonths(12);

        // When
        EmiPreviewResponse response = repaymentScheduleService.previewEmi(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getEmi() > 0);
        assertEquals(response.getEmi() * 12, response.getTotalPayment(), 0.01);
        assertEquals(response.getTotalPayment() - request.getAmount(),
                response.getTotalInterest(), 0.01);
        assertTrue(response.getTotalInterest() > 0); // Interest should be positive
    }

    @Test
    void testPreviewEmi_LongTenure() {
        // Given
        EmiPreviewRequest request = new EmiPreviewRequest();
        request.setAmount(500000.0);
        request.setAnnualInterestRate(8.5);
        request.setTenureMonths(60); // 5 years

        // When
        EmiPreviewResponse response = repaymentScheduleService.previewEmi(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getEmi() > 0);
        assertTrue(response.getTotalInterest() > 0);
        assertTrue(response.getTotalPayment() > request.getAmount());
    }

    @Test
    void testGetSchedule_WithExistingSchedules() {
        // Given
        Long loanId = 1L;
        when(loanType.getInterestRate()).thenReturn(10.0);
        List<RepaymentSchedule> mockSchedules = createMockSchedules(loanId, 12);
        when(repaymentScheduleRepository.findByLoan_Id(loanId)).thenReturn(mockSchedules);

        // When
        List<RepaymentScheduleDTO> result = repaymentScheduleService.getSchedule(loanId);

        // Then
        assertNotNull(result);
        assertEquals(12, result.size());

        RepaymentScheduleDTO firstDto = result.get(0);
        assertEquals(1, firstDto.getMonth());
        assertNotNull(firstDto.getEmi());
        assertNotNull(firstDto.getPrincipalAmount());
        assertNotNull(firstDto.getInterestAmount());
        assertNotNull(firstDto.getBalanceRemaining());
        assertFalse(firstDto.getIsPaid());

        verify(repaymentScheduleRepository).findByLoan_Id(loanId);
    }

    @Test
    void testGetSchedule_EmptySchedules() {
        // Given
        Long loanId = 1L;
        when(repaymentScheduleRepository.findByLoan_Id(loanId)).thenReturn(List.of());

        // When
        List<RepaymentScheduleDTO> result = repaymentScheduleService.getSchedule(loanId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repaymentScheduleRepository).findByLoan_Id(loanId);
    }

    @Test
    void testGetSchedule_AllFieldsMapped() {
        // Given
        Long loanId = 1L;
        when(loanType.getInterestRate()).thenReturn(10.0);
        List<RepaymentSchedule> mockSchedules = createMockSchedules(loanId, 3);
        when(repaymentScheduleRepository.findByLoan_Id(loanId)).thenReturn(mockSchedules);

        // When
        List<RepaymentScheduleDTO> result = repaymentScheduleService.getSchedule(loanId);

        // Then
        for (int i = 0; i < result.size(); i++) {
            RepaymentScheduleDTO dto = result.get(i);
            RepaymentSchedule schedule = mockSchedules.get(i);

            assertEquals(schedule.getId(), dto.getId());
            assertEquals(schedule.getMonth(), dto.getMonth());
            assertEquals(schedule.getPrincipalAmount(), dto.getPrincipalAmount());
            assertEquals(schedule.getInterestAmount(), dto.getInterestAmount());
            assertEquals(schedule.getBalanceRemaining(), dto.getBalanceRemaining());
            assertEquals(schedule.getIsPaid(), dto.getIsPaid());
        }
    }

    @Test
    void testPay_Success() {
        // Given
        Long loanId = 1L;
        Long scheduleId = 100L;

        RepaymentSchedule schedule = new RepaymentSchedule();
        schedule.setId(scheduleId);
        schedule.setLoan(loanApplication);
        schedule.setIsPaid(false);

        when(repaymentScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repaymentScheduleRepository.save(any(RepaymentSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        repaymentScheduleService.pay(loanId, scheduleId);

        // Then
        assertTrue(schedule.getIsPaid());
        verify(repaymentScheduleRepository).findById(scheduleId);
        verify(repaymentScheduleRepository).save(schedule);
    }

    @Test
    void testPay_ScheduleNotFound() {
        // Given
        Long loanId = 1L;
        Long scheduleId = 100L;
        when(repaymentScheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repaymentScheduleService.pay(loanId, scheduleId);
        });

        assertEquals("Schedule not found with id: " + scheduleId, exception.getMessage());
        verify(repaymentScheduleRepository).findById(scheduleId);
        verify(repaymentScheduleRepository, never()).save(any());
    }

    @Test
    void testPay_WrongLoanId() {
        // Given
        Long correctLoanId = 1L;
        Long wrongLoanId = 999L;
        Long scheduleId = 100L;

        RepaymentSchedule schedule = new RepaymentSchedule();
        schedule.setId(scheduleId);
        schedule.setLoan(loanApplication);
        schedule.setIsPaid(false);

        when(repaymentScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repaymentScheduleService.pay(wrongLoanId, scheduleId);
        });

        assertEquals("This schedule does not belong to loan id: " + wrongLoanId,
                exception.getMessage());
        assertFalse(schedule.getIsPaid()); // Should not be marked as paid
        verify(repaymentScheduleRepository).findById(scheduleId);
        verify(repaymentScheduleRepository, never()).save(any());
    }

    @Test
    void testPay_AlreadyPaid() {
        // Given
        Long loanId = 1L;
        Long scheduleId = 100L;

        RepaymentSchedule schedule = new RepaymentSchedule();
        schedule.setId(scheduleId);
        schedule.setLoan(loanApplication);
        schedule.setIsPaid(true); // Already paid

        when(repaymentScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repaymentScheduleRepository.save(any(RepaymentSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        repaymentScheduleService.pay(loanId, scheduleId);

        // Then
        assertTrue(schedule.getIsPaid()); // Still true
        verify(repaymentScheduleRepository).save(schedule);
    }

    // Helper method to create mock schedules
    private List<RepaymentSchedule> createMockSchedules(Long loanId, int count) {
        List<RepaymentSchedule> schedules = new ArrayList<>();
        double balance = loanApplication.getAmount();
        double monthlyPayment = balance / count;

        for (int i = 1; i <= count; i++) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setId((long) i);
            schedule.setLoan(loanApplication);
            schedule.setMonth(i);
            schedule.setPrincipalAmount(monthlyPayment * 0.8); // 80% principal
            schedule.setInterestAmount(monthlyPayment * 0.2); // 20% interest
            balance -= schedule.getPrincipalAmount();
            schedule.setBalanceRemaining(Math.max(balance, 0));
            schedule.setIsPaid(false);
            schedules.add(schedule);
        }

        return schedules;
    }
}