package tech.zetapioneers.loan_application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.entities.User;
//import tech.zetapioneers.loan_application.LoanApplication;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket,Long> {
      List<SupportTicket> findByUser(User user);
      List<SupportTicket> findByStatus(TicketStatus status);
}
