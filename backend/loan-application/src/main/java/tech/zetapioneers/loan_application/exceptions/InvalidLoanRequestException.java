package tech.zetapioneers.loan_application.exceptions;

public class InvalidLoanRequestException extends RuntimeException{
   public InvalidLoanRequestException(String message){
       super(message);
   }
}
