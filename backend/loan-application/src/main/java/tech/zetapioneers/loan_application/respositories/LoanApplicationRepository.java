package tech.zetapioneers.loan_application.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zetapioneers.loan_application.Entities.LoanApplication;
import tech.zetapioneers.loan_application.Entities.User;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, User> {
    List<LoanApplication> findByStatus(String status);
    List<LoanApplication> findByUser(User user);



}

