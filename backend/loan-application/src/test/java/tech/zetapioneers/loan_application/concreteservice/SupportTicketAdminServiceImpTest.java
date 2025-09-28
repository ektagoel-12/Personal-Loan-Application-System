package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseBodyDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.enums.TicketRequestTypes;
import tech.zetapioneers.loan_application.enums.TicketStatus;
import tech.zetapioneers.loan_application.exceptions.TicketNotFoundException;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketAdminServiceImpTest {
    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportTicketAdminServiceImp serviceImp;

    private User user1, user2, user3;

    private List<SupportTicket> supportTickets=new ArrayList<>();
    @BeforeEach
    void setup() {

        user1 = new User();
        user2 = new User();
        user3 = new User();

        user1.setId(1L);
        user1.setEmail("prem1@gmail.com");
        user1.setAadhar("989765749323");
        user1.setIncome(100.0);
        user1.setCreditScore(599);
        user1.setRole(Role.USER);

        user2.setId(2L);
        user2.setEmail("prem2@gmail.com");
        user2.setAadhar("989765949375");
        user2.setIncome(1000.0);
        user2.setCreditScore(689);
        user2.setRole(Role.ADMIN);

        user3.setId(3L);
        user3.setEmail("prem2@gmail.com");
        user3.setAadhar("989765949375");
        user3.setIncome(1000.0);
        user3.setCreditScore(689);
        user3.setRole(Role.ADMIN);

        supportTickets.add(new SupportTicket(1L,user1,null,"Test1","Testing the support ticket service component Test1",
                TicketRequestTypes.Others, TicketStatus.OPEN, LocalDateTime.now(), "No response from admin for Test1"));
        supportTickets.add(new SupportTicket(2L,user2,null,"Test2","Testing the support ticket service component Test2",
                TicketRequestTypes.Application_Status, TicketStatus.CLOSED, LocalDateTime.now(), "No response from admin Test2"));
        supportTickets.add(new SupportTicket(3L,user3,null,"Test3","Testing the support ticket service component Test3",
                TicketRequestTypes.Loan_Closure, TicketStatus.RESOLVED, LocalDateTime.now(), "No response from admin Test3"));
    }

    @Test
    void getAllTickets() {
        when(supportTicketRepository.findAll()).thenReturn(supportTickets);

        ResponseEntity<List<SupportTicketResponseDto>> response = serviceImp.getAllTickets();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());

        SupportTicketResponseDto dto = response.getBody().get(1); //second obj

        assertEquals(2L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Test2", dto.getSubject());
        assertEquals("Testing the support ticket service component Test2", dto.getDescription());
        assertEquals(TicketRequestTypes.Application_Status, dto.getType());
        assertEquals(TicketStatus.CLOSED, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("No response from admin Test2", dto.getResponse());

    }
    @Test
    void findTicketsByStatus() {
        TicketStatus status=TicketStatus.CLOSED;
        when(supportTicketRepository.findByStatus(ArgumentMatchers.<TicketStatus>any())).thenReturn(supportTickets.stream()
                .filter(supportTicket -> supportTicket.getStatus().equals(status)).toList());

        ResponseEntity<List<SupportTicketResponseDto>> response=serviceImp.findTicketsByStatus(status);

        SupportTicketResponseDto dto = response.getBody().get(0); //second obj

        assertEquals(2L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Test2", dto.getSubject());
        assertEquals("Testing the support ticket service component Test2", dto.getDescription());
        assertEquals(TicketRequestTypes.Application_Status, dto.getType());
        assertEquals(TicketStatus.CLOSED, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("No response from admin Test2", dto.getResponse());


    }

    @Test
    void addOrUpdateResponse() {
        Long ticketId=1L;
        String res="Updating the response fro the ticket: "+ticketId;
        LocalDateTime updateTime=supportTickets.stream().filter(ticket->ticket.getId().equals(ticketId)).toList().get(0).getUpdatedAt();
        SupportTicketResponseBodyDto supportTicketResponseBodyDto=new SupportTicketResponseBodyDto(res);

        when(supportTicketRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(supportTickets.stream()
                .filter(supportTicket -> supportTicket.getId().equals(ticketId)).toList().get(0)));
        when(supportTicketRepository.save(ArgumentMatchers.<SupportTicket>any()))
                .thenAnswer(invocation -> {
                    SupportTicket ticket = invocation.getArgument(0);
                    ticket.setResponse(res);
                    ticket.setUpdatedAt(LocalDateTime.now());
                    return ticket;
                });

        ResponseEntity<SupportTicketResponseDto> response=serviceImp.addOrUpdateResponse(ticketId,supportTicketResponseBodyDto);
        SupportTicketResponseDto dto=response.getBody();
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Test1", dto.getSubject());
        assertEquals("Testing the support ticket service component Test1", dto.getDescription());
        assertEquals(TicketRequestTypes.Others, dto.getType());
        assertEquals(TicketStatus.OPEN, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotEquals(updateTime,dto.getUpdatedAt());
        assertEquals(res, dto.getResponse());
    }


    @Test
    void updateTicketStatus() {
        Long ticketId=1L;
        TicketStatus status=TicketStatus.RESOLVED;
        LocalDateTime updateTime=supportTickets.stream().filter(ticket->ticket.getId().equals(ticketId)).toList().get(0).getUpdatedAt();

        when(supportTicketRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(supportTickets.stream()
                .filter(supportTicket -> supportTicket.getId().equals(ticketId)).toList().get(0)));
        when(supportTicketRepository.save(ArgumentMatchers.<SupportTicket>any()))
                .thenAnswer(invocation -> {
                    SupportTicket ticket = invocation.getArgument(0);
                    ticket.setStatus(status);
                    ticket.setUpdatedAt(LocalDateTime.now());
                    return ticket;
                });

        ResponseEntity<SupportTicketResponseDto> response=serviceImp.updateTicketStatus(ticketId,status);
        SupportTicketResponseDto dto=response.getBody();
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Test1", dto.getSubject());
        assertEquals("Testing the support ticket service component Test1", dto.getDescription());
        assertEquals(TicketRequestTypes.Others, dto.getType());
        assertEquals(TicketStatus.RESOLVED, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotEquals(updateTime,dto.getUpdatedAt());
    }

    @Test
    void getTicketByID() {
        Long ticketId=1L;
        SupportTicket temp=supportTickets.stream().filter(ticket -> ticket.getId().equals(ticketId)).toList().get(0);
        when(supportTicketRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(temp));
        ResponseEntity<SupportTicket> response=serviceImp.getTicketByID(ticketId);
        assertEquals(temp,response.getBody());
    }

    @Test
    void getTicketByIdWithError() {
        Long ticketId=990000L;
        assertThrows(TicketNotFoundException.class,()->serviceImp.getTicketByID(ticketId));
    }
}