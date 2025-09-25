package tech.zetapioneers.loan_application.concreteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseBodyDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.enums.TicketStatus;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;
import tech.zetapioneers.loan_application.services.SupportTicketAdminService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketAdminServiceImp implements SupportTicketAdminService {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    //  map SupportTicket → SupportTicketResponseDto
    private SupportTicketResponseDto mapToDto(SupportTicket ticket) {
        SupportTicketResponseDto dto = new SupportTicketResponseDto();
        dto.setId(ticket.getId());
        dto.setUserEmail(ticket.getUser().getEmail());
        dto.setLoanId(ticket.getLoan() != null ? ticket.getLoan().getId() : null);
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setCreateAt(ticket.getCreateAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResponse(ticket.getResponse());
        return dto;
    }

    @Override
    public ResponseEntity<List<SupportTicketResponseDto>> getAllTickets() {
        List<SupportTicketResponseDto> ticketDtoList = supportTicketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(ticketDtoList);
    }

    @Override
    public ResponseEntity<List<SupportTicketResponseDto>> findTicketsByStatus(TicketStatus status) {
        List<SupportTicketResponseDto> response = supportTicketRepository.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SupportTicketResponseDto> addOrUpdateResponse(Long ticketId, SupportTicketResponseBodyDto responseBody) {
        SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        supportTicket.setResponse(responseBody.getResponse());
        supportTicket.setUpdatedAt(LocalDateTime.now());

        SupportTicket savedTicket = supportTicketRepository.save(supportTicket);

        return ResponseEntity.ok(mapToDto(savedTicket));
    }

    @Override
    public ResponseEntity<SupportTicketResponseDto> updateTicketStatus(Long ticketId, TicketStatus status) {
        SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        supportTicket.setStatus(status);
        supportTicket.setUpdatedAt(LocalDateTime.now());

        SupportTicket savedTicket = supportTicketRepository.save(supportTicket);

        return ResponseEntity.ok(mapToDto(savedTicket));
    }
}
