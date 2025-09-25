package tech.zetapioneers.loan_application.enums;

public enum LoanType {
    HOME_LOAN(7.5),
    PERSONAL_LOAN(10.0),
    CAR_LOAN(8.0),
    EDUCATION_LOAN(6.5),
    BUSINESS_LOAN(12.5),
    GOLD_LOAN(9.0),
    CREDIT_CARD_LOAN(18.0),
    PAYDAY_LOAN(30.0),
    HOME_EQUITY_LOAN(8.5),
    STUDENT_LOAN(5.0);

    private final double interestRate;

    // Constructor
    LoanType(double interestRate) {
        this.interestRate = interestRate;
    }

    // Getter
    public double getInterestRate() {
        return interestRate;
    }
}
