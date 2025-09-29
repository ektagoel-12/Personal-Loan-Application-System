package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.concreteservice.SupportTicketAdminServiceImpl;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseBodyDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.enums.TicketStatus;

import java.util.List;

@RestController
@RequestMapping("/ticket/admin")
public class SupportTicketAdminController {

    @Autowired
    private SupportTicketAdminServiceImpl supportTicketAdminServiceImp;

    @GetMapping
    public ResponseEntity<List<SupportTicketResponseDto>> getAllTickets() {
        return supportTicketAdminServiceImp.getAllTickets();
    }

    @GetMapping("ticketId/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id){
        return supportTicketAdminServiceImp.getTicketByID(id);
    }

    @GetMapping("status/{status}")
    public ResponseEntity<List<SupportTicketResponseDto>> findTicketsByStatus(@PathVariable TicketStatus status) {
        return supportTicketAdminServiceImp.findTicketsByStatus(status);
    }

    // Add or update response for a ticket
    @PatchMapping("/{ticketId}/response")
    public ResponseEntity<SupportTicketResponseDto> addOrUpdateResponse(
            @PathVariable Long ticketId,
            @RequestBody SupportTicketResponseBodyDto responseBody) {
        return supportTicketAdminServiceImp.addOrUpdateResponse(ticketId, responseBody);
    }

    // Update ticket status (OPEN, CLOSED, PENDING)
    @PatchMapping("/{ticketId}/{status}")
    public ResponseEntity<SupportTicketResponseDto> updateTicketStatus(
            @PathVariable Long ticketId,
            @PathVariable TicketStatus status) {
        return supportTicketAdminServiceImp.updateTicketStatus(ticketId, status);
    }
}
