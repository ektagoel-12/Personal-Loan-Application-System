package tech.zetapioneers.loan_application.concreteservice;


import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationService {
    private LoanApplicationRepository loanApplicationRepository;
    private UserRepository userRepository;

    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository,UserRepository userRepository){
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
    }

    public Long addLoanApplication(LoanApplicationResponse loanApplicationResponse){
        LoanApplication loanApplication = new LoanApplication();
        User user = userRepository.findById(loanApplicationResponse.getUserId()).get();
        loanApplication.setUser(user);
        loanApplication.setAmount(loanApplicationResponse.getAmount());
        loanApplication.setTenureMonths(loanApplicationResponse.getTenure());
        loanApplication.setIncome( loanApplicationResponse.getIncome());
        loanApplication.setStatus(loanApplicationResponse.getStatus());
        loanApplication.setApplicationDate(loanApplicationResponse.getApplicationDate());
        loanApplication.setType(loanApplicationResponse.getPurpose());
        return loanApplicationRepository.save(loanApplication).getId();
    }

    public List<LoanApplicationResponse> getLoanByUser(Long userId){
        User user = userRepository.findById(userId).get();
        List<LoanApplicationResponse> loanList = new ArrayList<>();
        List<LoanApplication> loans = loanApplicationRepository.findAllByUser(user);
        LoanApplicationResponse loanApplicationResponse;
        for(LoanApplication loan : loans){
            loanApplicationResponse = new LoanApplicationResponse();
            loanApplicationResponse.setId(loan.getId());
            loanApplicationResponse.setUserId(loan.getUser().getId());
            loanApplicationResponse.setName(loan.getUser().getName());
            loanApplicationResponse.setCreditScore(loan.getCreditScore());
            loanApplicationResponse.setIncome( loan.getIncome());
            loanApplicationResponse.setAmount(loan.getAmount());
            loanApplicationResponse.setPurpose(loan.getType());
            loanApplicationResponse.setStatus(loan.getStatus());
            loanApplicationResponse.setApplicationDate(loan.getApplicationDate());
            loanApplicationResponse.setLastUpdated(loan.getReviewedAt());
            loanApplicationResponse.setRateOfInterest(loan.getType().getInterestRate());
            loanApplicationResponse.setTenure(loan.getTenureMonths());
            loanApplicationResponse.setRemarks(loan.getReviewRemarks());
            loanApplicationResponse.setRemarksBy(loan.getReviewedBy());
            
            loanList.add(loanApplicationResponse);
        }
        return loanList;
    }

    public LoanApplicationResponse getLoanById(Long loanId){
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId).get();
        LoanApplicationResponse loanApplicationResponse = new LoanApplicationResponse();
        loanApplicationResponse.setId(loanApplication.getId());
        loanApplicationResponse.setUserId(loanApplication.getUser().getId());
        loanApplicationResponse.setName(loanApplication.getUser().getName());
        loanApplicationResponse.setCreditScore(loanApplication.getCreditScore());
        loanApplicationResponse.setIncome( loanApplication.getIncome());
        loanApplicationResponse.setAmount(loanApplication.getAmount());
        loanApplicationResponse.setPurpose(loanApplication.getType());
        loanApplicationResponse.setStatus(loanApplication.getStatus());
        loanApplicationResponse.setApplicationDate(loanApplication.getApplicationDate());
        loanApplicationResponse.setLastUpdated(loanApplication.getReviewedAt());
        loanApplicationResponse.setRateOfInterest(loanApplication.getType().getInterestRate());
        loanApplicationResponse.setTenure(loanApplication.getTenureMonths());
        loanApplicationResponse.setRemarks(loanApplication.getReviewRemarks());
        loanApplicationResponse.setRemarksBy(loanApplication.getReviewedBy());
        return loanApplicationResponse;
    }


}
