package tech.zetapioneers.loan_application.exceptions;

public class TicketNotFoundException extends RuntimeException{
   public TicketNotFoundException(String message){
       super(message);
   }
}
