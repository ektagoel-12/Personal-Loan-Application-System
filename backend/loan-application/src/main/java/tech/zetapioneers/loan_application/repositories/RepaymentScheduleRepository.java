package tech.zetapioneers.loan_application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zetapioneers.loan_application.entities.RepaymentSchedule;

import java.util.List;
@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule,Long> {
    List<RepaymentSchedule> findByLoan_Id(Long loanId);

}
