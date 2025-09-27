package tech.zetapioneers.loan_application.services;

import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketRequestDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;

import java.util.List;

public interface SupportTicketUserService {

    //createTicket
    ResponseEntity<SupportTicketResponseDto> createTicket(SupportTicketRequestDto supportTicketDto);
    //getTicketsByUser
    ResponseEntity<List<SupportTicketResponseDto>>  getTicketsByUser(String email);

    ResponseEntity<SupportTicket> getTicketByID(Long id);
}
