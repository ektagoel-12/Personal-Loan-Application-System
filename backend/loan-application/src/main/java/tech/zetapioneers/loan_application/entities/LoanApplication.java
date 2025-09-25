package tech.zetapioneers.loan_application.entities;

import jakarta.persistence.*;
import lombok.Data;
import tech.zetapioneers.loan_application.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount; //
    private Integer tenureMonths;
    private Double income; //
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    private Status status; //

    private LocalDate applicationDate; //
    private String purpose;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy; //

    private LocalDateTime reviewedAt; //
    private String reviewRemarks; //

    // getters and setters
}
