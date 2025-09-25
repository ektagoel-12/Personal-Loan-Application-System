package tech.zetapioneers.loan_application.entities;

import jakarta.persistence.*;
import lombok.Data;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ticket_id", nullable = false)
    private Long id;

    // *****update the frontend accordingly******
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private LoanApplication loan;

    private String subject;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    private final LocalDateTime createAt=LocalDateTime.now();

    private LocalDateTime updatedAt;


    private String response;


}

