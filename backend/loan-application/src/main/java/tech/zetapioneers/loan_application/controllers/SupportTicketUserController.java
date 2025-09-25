package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;
import tech.zetapioneers.loan_application.concreteservice.SupportTicketUserServiceImp;

import java.util.List;

@RestController @RequestMapping("/ticket/user")
public class SupportTicketUserController {
     @Autowired
     SupportTicketUserServiceImp supportTicketUserServiceImp;


    @PostMapping public ResponseEntity<SupportTicketDto> createTicket(SupportTicketDto supportTicketDto) {
      return  supportTicketUserServiceImp.createTicket(supportTicketDto);
    }


    @GetMapping("/{userEmail}") public ResponseEntity<List<SupportTicketDto>> getAllTicketsByUserId(@PathVariable String userEmail){
        return supportTicketUserServiceImp.getTicketsByUser(userEmail);
    }
}
