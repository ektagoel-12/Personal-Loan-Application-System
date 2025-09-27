package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.SupportTicketRequestDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.concreteservice.SupportTicketUserServiceImp;
import tech.zetapioneers.loan_application.entities.SupportTicket;

import java.util.List;

@RestController
@RequestMapping("/ticket/user")
public class SupportTicketUserController {

    @Autowired
    private SupportTicketUserServiceImp supportTicketUserServiceImp;


    @PostMapping
    public ResponseEntity<SupportTicketResponseDto> createTicket(
            @RequestBody SupportTicketRequestDto supportTicketDto) {
        return supportTicketUserServiceImp.createTicket(supportTicketDto);
    }


    @GetMapping("/{userEmail}")
    public ResponseEntity<List<SupportTicketResponseDto>> getAllTicketsByUserEmail(
            @PathVariable String userEmail) {
        return supportTicketUserServiceImp.getTicketsByUser(userEmail);
    }

    @PostMapping("/id")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id){
        return supportTicketUserServiceImp.getTicketByID(id);
    }
}
