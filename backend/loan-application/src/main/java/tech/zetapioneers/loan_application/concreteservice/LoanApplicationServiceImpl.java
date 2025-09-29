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
public class LoanApplicationServiceImpl {
    private LoanApplicationRepository loanApplicationRepository;
    private UserRepository userRepository;

    public LoanApplicationServiceImpl(LoanApplicationRepository loanApplicationRepository, UserRepository userRepository){
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
    }

    public Long addLoanApplication(LoanApplicationResponse loanApplicationResponse){
        LoanApplication loanApplication = new LoanApplication();
        User user = userRepository.findById(loanApplicationResponse.getUserId()).get();
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
