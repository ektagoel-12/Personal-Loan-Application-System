package tech.zetapioneers.loan_application.services;

import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketRequestDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseBodyDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.util.List;

public interface SupportTicketAdminService {

        ResponseEntity<List<SupportTicketResponseDto>> getAllTickets();

        // Find tickets by status
        ResponseEntity<List<SupportTicketResponseDto>> findTicketsByStatus(TicketStatus status);

        // Add or update response for a ticket
        ResponseEntity<SupportTicketResponseDto> addOrUpdateResponse(Long ticketId, SupportTicketResponseBodyDto response);

        // Update ticket status (OPEN, CLOSED, PENDING)
        ResponseEntity<SupportTicketResponseDto>  updateTicketStatus(Long ticketId, TicketStatus status);

        ResponseEntity<SupportTicket> getTicketByID(Long id);
    }


