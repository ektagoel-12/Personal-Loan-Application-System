package tech.zetapioneers.loan_application.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "repayment_schedule")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanApplication loan;

    private Integer month;
    private Double principalAmount;
    private Double interestAmount;
    private Double balanceRemaining;

    // getters and setters
}

