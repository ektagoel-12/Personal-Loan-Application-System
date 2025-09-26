package tech.zetapioneers.loan_application.concreteservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.EmiPreviewRequest;
import tech.zetapioneers.loan_application.dto.EmiPreviewResponse;
import tech.zetapioneers.loan_application.dto.RepaymentScheduleDTO;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;
import tech.zetapioneers.loan_application.repositories.RepaymentScheduleRepository;
import tech.zetapioneers.loan_application.services.RepaymentScheduleService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleServiceImpl implements RepaymentScheduleService {

    private final RepaymentScheduleRepository repaymentScheduleRepository;

    @Override
    public double calculateEMI(double principal, double annualRate, int months) {
        double monthlyRate = annualRate / 12 / 100; // Convert annual % to monthly decimal
        return (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                (Math.pow(1 + monthlyRate, months) - 1);
    }

    @Override
    public List<RepaymentSchedule> generateSchedule(LoanApplication loan) {
        // pull the rate from LoanType enum
        System.out.println(loan);
        double annualRate = loan.getLoanType().getInterestRate();

        double principal = loan.getAmount();
        int months = loan.getTenureMonths();
        double emi = calculateEMI(principal, annualRate, months);
        double monthlyRate = annualRate / 12 / 100;

        List<RepaymentSchedule> schedules = new ArrayList<>();
        double balance = principal;

        for (int m = 1; m <= months; m++) {
            double interest = balance * monthlyRate;
            double principalComponent = emi - interest;
            balance -= principalComponent;

            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoan(loan);
            schedule.setMonth(m);
            schedule.setPrincipalAmount(principalComponent);
            schedule.setInterestAmount(interest);
            schedule.setBalanceRemaining(Math.max(balance, 0));
            schedule.setIsPaid(false);   // always initialize as unpaid

            schedules.add(schedule);
        }

        return repaymentScheduleRepository.saveAll(schedules);
    }

    @Override
    public EmiPreviewResponse previewEmi(EmiPreviewRequest request) {
        double emi = calculateEMI(request.getAmount(), request.getAnnualInterestRate(), request.getTenureMonths());
        double totalPayment = emi * request.getTenureMonths();
        double totalInterest = totalPayment - request.getAmount();

        EmiPreviewResponse response = new EmiPreviewResponse();
        response.setEmi(emi);
        response.setTotalPayment(totalPayment);
        response.setTotalInterest(totalInterest);

        return response;
    }

    @Override
    public List<RepaymentScheduleDTO> getSchedule(Long loanId) {
        List<RepaymentSchedule> schedules = repaymentScheduleRepository.findByLoan_Id(loanId);

        // Fetch loan details (principal, rate, months)
        LoanApplication loan = schedules.get(0).getLoan();
        double emi = calculateEMI(
                loan.getAmount(),
                loan.getLoanType().getInterestRate(),
                loan.getTenureMonths()
        );

        return schedules.stream()
                .map(s -> {
                    RepaymentScheduleDTO dto = new RepaymentScheduleDTO();
                    dto.setMonth(s.getMonth());
                    dto.setPrincipalAmount(s.getPrincipalAmount());
                    dto.setInterestAmount(s.getInterestAmount());
                    dto.setBalanceRemaining(s.getBalanceRemaining());

                    // Set EMI using formula (same each month)
                    dto.setEmi(emi);

                    return dto;
                })
                .toList();
    }

    @Override
    public void pay(Long loanId, Long scheduleId) {
        RepaymentSchedule schedule = repaymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        if (!schedule.getLoan().getId().equals(loanId)) {
            throw new RuntimeException("This schedule does not belong to loan id: " + loanId);
        }

        schedule.setIsPaid(true);
        repaymentScheduleRepository.save(schedule);
    }
}
