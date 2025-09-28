package tech.zetapioneers.loan_application.concreteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.SupportTicketRequestDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.LoanApplication;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.TicketRequestTypes;
import tech.zetapioneers.loan_application.exceptions.LoanNotFoundException;
import tech.zetapioneers.loan_application.exceptions.TicketNotFoundException;
import tech.zetapioneers.loan_application.exceptions.UserNotFoundException;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.services.SupportTicketUserService;

import java.util.List;
@Service
public class SupportTicketUserServiceImp implements SupportTicketUserService {

    @Autowired
    SupportTicketRepository supportTicketRepository;
    @Autowired
    LoanApplicationRepository loanApplicationRepository;
    @Autowired
    UserRepository userRepository;
    //  map SupportTicket → SupportTicketResponseDto
    private SupportTicketResponseDto mapToDto(SupportTicket ticket) {
        SupportTicketResponseDto dto = new SupportTicketResponseDto();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUser().getId());
        dto.setLoanId(ticket.getLoan() != null ? ticket.getLoan().getId() : null);
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setType(ticket.getType());
        dto.setStatus(ticket.getStatus());
        dto.setCreateAt(ticket.getCreateAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResponse(ticket.getResponse());
        return dto;
    }

        @Override
        public ResponseEntity<SupportTicketResponseDto> createTicket(SupportTicketRequestDto dto) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found id: "+ dto.getUserId()));

            LoanApplication loan = null;
            if (dto.getLoanId() != null) {
                loan = loanApplicationRepository.findById(dto.getLoanId())
                        .orElseThrow(()->new LoanNotFoundException("Loan is not found with id: "+dto.getLoanId()));
            }

            SupportTicket ticket = new SupportTicket();
            ticket.setUser(user);
            ticket.setLoan(loan);
            ticket.setType(dto.getType());
            ticket.setSubject(dto.getSubject());
            ticket.setDescription(dto.getDescription());
            SupportTicket savedTicket = supportTicketRepository.save(ticket);

            // mapping to ResponseDto
            SupportTicketResponseDto responseDto = this.mapToDto(savedTicket);

            return ResponseEntity.status(201).body(responseDto);
        }

        @Override
        public ResponseEntity<List<SupportTicketResponseDto>> getTicketsByUser(String email) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: "+ email));

            List<SupportTicket> tickets = supportTicketRepository.findByUser(user);

            List<SupportTicketResponseDto> response = tickets.stream()
                    .map(this::mapToDto).toList();

            return ResponseEntity.ok(response);
        }

    @Override
    public ResponseEntity<SupportTicket> getTicketByID(Long id) {
        SupportTicket supportTicket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
        return ResponseEntity.status(200).body(supportTicket);
    }

}