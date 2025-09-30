package tech.zetapioneers.loan_application.concreteservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.exceptions.InvalidLoanRepuestException;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.RepaymentScheduleRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanApplicationServiceImpl {
    private LoanApplicationRepository loanApplicationRepository;
    private UserRepository userRepository;
    @Autowired
    RepaymentScheduleRepository repaymentScheduleRepository;

    public LoanApplicationServiceImpl(LoanApplicationRepository loanApplicationRepository, UserRepository userRepository){
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
    }

    public Long addLoanApplication(LoanApplicationResponse loanApplicationResponse){
       //active loan <=5 -->updated
       //requested loan amount <= income*60 (5 years total monthly income) - total already approved loan amount

        LoanApplication loanApplication = new LoanApplication();
        User user = userRepository.findById(loanApplicationResponse.getUserId()).get();

        List<LoanApplication> userLoans=loanApplicationRepository.findAllByUser(user);
        if(userLoans!=null) {
            List<LoanApplication> activeApprovedLoans = userLoans.stream()
                    .filter(loan -> loan.getStatus() == LoanStatus.APPROVED) // approved loans
                    .filter(loan ->
                            repaymentScheduleRepository.findByLoan_Id(loan.getId()).stream()
                                    .anyMatch(schedule -> schedule.getBalanceRemaining() > 0 && !schedule.getIsPaid())
                    ).toList();
            double totalSum = activeApprovedLoans.stream()
                    .map(LoanApplication::getAmount)
                    .reduce(0.0, (sum, amount) -> sum + amount);
            double approvalAmount = loanApplicationResponse.getIncome() * 120;   //5 years range

            if (activeApprovedLoans.size() >= 5)
                throw new InvalidLoanRepuestException("Maximum active loan should be less than 5.");
            if (loanApplicationResponse.getAmount() > approvalAmount - totalSum)
                throw new InvalidLoanRepuestException("Exceed the loan amount limit");
        }
        loanApplication.setUser(user);
        loanApplication.setAmount(loanApplicationResponse.getAmount());
        loanApplication.setTenureMonths(loanApplicationResponse.getTenure()*12);
        loanApplication.setIncome( loanApplicationResponse.getIncome());
        loanApplication.setCreditScore(loanApplicationResponse.getCreditScore());
        loanApplication.setStatus(loanApplicationResponse.getStatus());
        loanApplication.setApplicationDate(loanApplicationResponse.getApplicationDate());
        loanApplication.setLoanType(loanApplicationResponse.getPurpose());

        return loanApplicationRepository.save(loanApplication).getId();
    }

    public List<LoanApplicationResponse> getAllLoans(){
        List<LoanApplicationResponse> loanList = new ArrayList<>();
        List<LoanApplication> loans = loanApplicationRepository.findAll();
        LoanApplicationResponse loanApplicationResponse;
        for(LoanApplication loan : loans){
            loanList.add(entityToDto(loan));
        }
        return loanList;
    }

    public List<LoanApplicationResponse> getLoanByUser(Long userId){
        User user = userRepository.findById(userId).get();
        List<LoanApplicationResponse> loanList = new ArrayList<>();
        List<LoanApplication> loans = loanApplicationRepository.findAllByUser(user);
        LoanApplicationResponse loanApplicationResponse;
        for(LoanApplication loan : loans){
            loanList.add(entityToDto(loan));
        }
        return loanList;
    }

    public LoanApplicationResponse getLoanById(Long loanId){
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId).get();
        return entityToDto(loanApplication);
    }

    private static LoanApplicationResponse entityToDto(LoanApplication loanApplication){
        LoanApplicationResponse loanApplicationResponse = new LoanApplicationResponse() ;
        loanApplicationResponse.setId(loanApplication.getId());
        loanApplicationResponse.setUserId(loanApplication.getUser().getId());
        loanApplicationResponse.setName(loanApplication.getUser().getName());
        loanApplicationResponse.setCreditScore(loanApplication.getCreditScore());
        loanApplicationResponse.setIncome( loanApplication.getIncome());
        loanApplicationResponse.setAmount(loanApplication.getAmount());
        loanApplicationResponse.setPurpose(loanApplication.getLoanType());
        loanApplicationResponse.setStatus(loanApplication.getStatus());
        loanApplicationResponse.setApplicationDate(loanApplication.getApplicationDate());
        loanApplicationResponse.setLastUpdated(loanApplication.getReviewedAt());
        loanApplicationResponse.setRateOfInterest(loanApplication.getLoanType().getInterestRate());
        loanApplicationResponse.setTenure(loanApplication.getTenureMonths());
        loanApplicationResponse.setRemarks(loanApplication.getReviewRemarks());
        loanApplicationResponse.setRemarksBy(loanApplication.getReviewedBy());
        return loanApplicationResponse;
    }


}
