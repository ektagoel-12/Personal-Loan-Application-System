package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.concreteservice.SupportTicketAdminServiceImp;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.util.List;

@RestController @RequestMapping("/ticket/admin")
public class SupportTicketAdminController {
    @Autowired
    SupportTicketAdminServiceImp supportTicketAdminServiceImp;

    @GetMapping
    public ResponseEntity<List<SupportTicketDto>> getAllTickets() {
      return supportTicketAdminServiceImp.getAllTickets();
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<SupportTicketDto>> findTicketsByStatus(@PathVariable TicketStatus status) {
     return supportTicketAdminServiceImp.findTicketsByStatus(status);
    }

    // Add or update response for a ticket
    @PutMapping("/{ticketId}/response")
    public ResponseEntity<SupportTicketDto> addOrUpdateResponse(@PathVariable Long ticketId, @RequestBody String response) {
      return supportTicketAdminServiceImp.addOrUpdateResponse(ticketId,response);
    }

    // Update ticket status (OPEN, CLOSED, PENDING)
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<SupportTicketDto>  updateTicketStatus(Long ticketId, TicketStatus status) {
      return supportTicketAdminServiceImp.updateTicketStatus(ticketId,status);
    }
}

