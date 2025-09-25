package tech.zetapioneers.loan_application.services;

import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.util.List;

public interface SupportTicketAdminService {

        ResponseEntity<List<SupportTicketDto>> getAllTickets();

        // Find tickets by status
        ResponseEntity<List<SupportTicketDto>> findTicketsByStatus(TicketStatus status);

        // Add or update response for a ticket
        ResponseEntity<SupportTicketDto> addOrUpdateResponse(Long ticketId, String response);

        // Update ticket status (OPEN, CLOSED, PENDING)
        ResponseEntity<SupportTicketDto>  updateTicketStatus(Long ticketId, TicketStatus status);
    }


