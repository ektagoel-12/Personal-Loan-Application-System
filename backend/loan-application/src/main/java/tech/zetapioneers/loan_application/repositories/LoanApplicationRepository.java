package tech.zetapioneers.loan_application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findAllByUser(User user);
}
