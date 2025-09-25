package tech.zetapioneers.loan_application.concreteservice;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.SupportTicketDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.enums.TicketStatus;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;
import tech.zetapioneers.loan_application.services.SupportTicketAdminService;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class SupportTicketAdminServiceImp implements SupportTicketAdminService {
    @Autowired
    SupportTicketRepository supportTicketRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public ResponseEntity<List<SupportTicketDto>> getAllTickets() {
        List<SupportTicket> ticketList=supportTicketRepository.findAll();
        List<SupportTicketDto> ticketDtoList=ticketList.stream().map(ticket->modelMapper.
                map(ticket,SupportTicketDto.class)).toList();
        return ResponseEntity.status(200).body(ticketDtoList);
    }

    @Override
    public ResponseEntity<List<SupportTicketDto>> findTicketsByStatus(TicketStatus status) {
        List<SupportTicketDto> response = supportTicketRepository.findByStatus(status)
                .stream()
                .map(ticket -> modelMapper.map(ticket, SupportTicketDto.class))
                .toList();

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<SupportTicketDto> addOrUpdateResponse(Long ticketId, String response) {
        SupportTicket supportTicket=supportTicketRepository.findById(ticketId)
                .orElseThrow(()->new RuntimeException("Ticket not found with id: "+ ticketId));
        supportTicket.setResponse(response);
        supportTicket.setUpdatedAt(LocalDateTime.now());
        SupportTicket supportTicket1=supportTicketRepository.save(supportTicket);
        return ResponseEntity.status(200).body(modelMapper.map(supportTicket1,SupportTicketDto.class));
    }

    @Override
    public ResponseEntity<SupportTicketDto> updateTicketStatus(Long ticketId, TicketStatus status) {
        SupportTicket supportTicket=supportTicketRepository.findById(ticketId)
                .orElseThrow(()->new RuntimeException("Ticket not found with id: "+ ticketId));
        supportTicket.setStatus(status);
        supportTicket.setUpdatedAt(LocalDateTime.now());
        SupportTicket supportTicket1=supportTicketRepository.save(supportTicket);
        return ResponseEntity.status(200).body(modelMapper.map(supportTicket1,SupportTicketDto.class));
    }
}
