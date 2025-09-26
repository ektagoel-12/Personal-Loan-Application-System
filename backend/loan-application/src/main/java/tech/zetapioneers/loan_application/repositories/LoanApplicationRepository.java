package tech.zetapioneers.loan_application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Status;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByStatus(Status status);

    long countByStatus(Status status);

    @Query("SELECT count(l) FROM LoanApplication l WHERE l.applicationDate BETWEEN :start AND :end")
    long countBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT EXTRACT(MONTH FROM l.applicationDate) as m, COUNT(l) as cnt " +
            "FROM LoanApplication l " +
            "WHERE l.applicationDate BETWEEN :start AND :end " +
            "GROUP BY EXTRACT(MONTH FROM l.applicationDate) " +
            "ORDER BY m")
    List<Object[]> monthlyCountsBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT l.status, COUNT(l) FROM LoanApplication l GROUP BY l.status")
    List<Object[]> statusDistribution();
}

