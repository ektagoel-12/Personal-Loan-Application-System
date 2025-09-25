package tech.zetapioneers.loan_application.services;

import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;

import java.util.List;

public interface SupportTicketUserService {

    //createTicket
    ResponseEntity<SupportTicketDto> createTicket(SupportTicketDto supportTicketDto);
    //getTicketsByUser
    ResponseEntity<List<SupportTicketDto>>  getTicketsByUser(String email);
}
