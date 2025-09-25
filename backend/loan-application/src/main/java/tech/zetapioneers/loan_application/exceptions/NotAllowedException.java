package tech.zetapioneers.loan_application.exceptions;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(String message){
        super(message);
    }
}
