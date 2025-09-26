package tech.zetapioneers.loan_application.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import tech.zetapioneers.loan_application.enums.LoanType;
import tech.zetapioneers.loan_application.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount;
    private Integer tenureMonths;
    private Double income;
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String reviewRemarks;

}
