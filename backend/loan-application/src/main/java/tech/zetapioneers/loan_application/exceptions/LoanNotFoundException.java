package tech.zetapioneers.loan_application.exceptions;

import javax.management.monitor.StringMonitor;

public class LoanNotFoundException extends RuntimeException{
    public LoanNotFoundException(String message){

        super(message);
    }
}
