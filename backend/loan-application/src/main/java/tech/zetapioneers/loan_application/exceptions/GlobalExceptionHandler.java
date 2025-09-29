package tech.zetapioneers.loan_application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex) { return ex.getMessage(); }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBad(BadRequestException ex) { return ex.getMessage(); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a,b)->a + ", " + b)
                .orElse("validation error");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        return new ResponseEntity<>(Map.of("error",userNotFoundException.getMessage()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException invalidCredentialsException){
        return new ResponseEntity<>(Map.of("error",invalidCredentialsException.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<?> handleUserRegistrationException(UserRegistrationException userRegistrationException){
        return new ResponseEntity<>(Map.of("error",userRegistrationException.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAllowedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleNotAllowedException(NotAllowedException notAllowedException){
        return new ResponseEntity<>(Map.of("error",notAllowedException.getMessage()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTicketNotFound(TicketNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Ticket Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleLoanNotFound(LoanNotFoundException ex){
        Map<String,Object> body=new HashMap<>();
        body.put("timestamp",LocalDateTime.now());
        body.put("error","Loan is not found");
        body.put("message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
