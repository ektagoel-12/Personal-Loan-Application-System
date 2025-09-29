package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import tech.zetapioneers.loan_application.dto.SupportTicketRequestDto;
import tech.zetapioneers.loan_application.dto.SupportTicketResponseDto;
import tech.zetapioneers.loan_application.entities.SupportTicket;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.enums.TicketRequestTypes;
import tech.zetapioneers.loan_application.enums.TicketStatus;
import tech.zetapioneers.loan_application.exceptions.LoanNotFoundException;
import tech.zetapioneers.loan_application.exceptions.TicketNotFoundException;
import tech.zetapioneers.loan_application.exceptions.UserNotFoundException;
import tech.zetapioneers.loan_application.repositories.LoanApplicationRepository;
import tech.zetapioneers.loan_application.repositories.SupportTicketRepository;
import tech.zetapioneers.loan_application.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketUserServiceImpTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;
    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SupportTicketUserServiceImpl serviceImp;

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
        user3.setEmail("prem3@gmail.com");
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
    void createTicket() {
        SupportTicketRequestDto requestDto=new SupportTicketRequestDto(
                1L,null,"Creating Ticket","Creating test for createTicket()",TicketRequestTypes.Application_Status);

        when(userRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(user1));


        when(supportTicketRepository.save(ArgumentMatchers.<SupportTicket>any()))
                .thenAnswer(invocation -> {
                  SupportTicket saveTicket=invocation.getArgument(0);
                  saveTicket.setId((long)(supportTickets.size()+1));
                  supportTickets.add(saveTicket);
                  return saveTicket;
                });

        ResponseEntity<SupportTicketResponseDto> response=serviceImp.createTicket(requestDto);
        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4,supportTickets.size());

        SupportTicketResponseDto dto = response.getBody(); //second obj

        assertEquals(supportTickets.size(),dto.getId());
        assertEquals(1L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Creating Ticket", dto.getSubject());
        assertEquals("Creating test for createTicket()", dto.getDescription());
        assertEquals(TicketRequestTypes.Application_Status, dto.getType());
        assertEquals(TicketStatus.OPEN, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("", dto.getResponse());
    }
    @Test
    void createTicketWithoutUserId() {
        SupportTicketRequestDto requestDto=new SupportTicketRequestDto(
                101L,null,"Creating Ticket","Creating test for createTicket()",TicketRequestTypes.Application_Status);
        assertThrows(UserNotFoundException.class,()->serviceImp.createTicket(requestDto));
    }
    @Test
    void createTicketWithInvalidLoan() {
        SupportTicketRequestDto requestDto=new SupportTicketRequestDto(
                1L,1001L,"Creating Ticket","Creating test for createTicket()",TicketRequestTypes.Application_Status);
        when(userRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(user1));
        when(loanApplicationRepository.findById(ArgumentMatchers.<Long>any()))
                .thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class,()->serviceImp.createTicket(requestDto));
    }

    @Test
    void getTicketsByUser() {
        String email="prem3@gmail.com";
        when(userRepository.findByEmail(ArgumentMatchers.<String>any())).thenReturn(Optional.ofNullable(user3));
        when(supportTicketRepository.findByUser(ArgumentMatchers.<User>any())).thenAnswer(invocation ->{
         List<SupportTicket> list=  supportTickets.stream().filter(ticket->ticket.getUser().equals(invocation.getArgument(0))).toList();
         return list;
        } );
        ResponseEntity<List<SupportTicketResponseDto>> response=serviceImp.getTicketsByUser(email);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());

        SupportTicketResponseDto dto = response.getBody().get(0); //second obj

        assertEquals(3L,dto.getId());
        assertEquals(3L, dto.getUserId());
        assertNull(dto.getLoanId());
        assertEquals("Test3", dto.getSubject());
        assertEquals("Testing the support ticket service component Test3", dto.getDescription());
        assertEquals(TicketRequestTypes.Loan_Closure, dto.getType());
        assertEquals(TicketStatus.RESOLVED, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("No response from admin Test3", dto.getResponse());
    }

    @Test
    void getTicketsByUserButUserNotFound() {
        when(userRepository.findByEmail(ArgumentMatchers.<String>any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,()->serviceImp.getTicketsByUser(""));
    }

    @Test
    void getTicketByID() {
        when(supportTicketRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.ofNullable(supportTickets.get(0)));

        ResponseEntity<SupportTicket> response=serviceImp.getTicketByID(1L);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        SupportTicket dto=response.getBody();
        assertEquals(1L,dto.getId());
        assertEquals(user1, dto.getUser());
        assertNull(dto.getLoan());
        assertEquals("Test1", dto.getSubject());
        assertEquals("Testing the support ticket service component Test1", dto.getDescription());
        assertEquals(TicketRequestTypes.Others, dto.getType());
        assertEquals(TicketStatus.OPEN, dto.getStatus());
        assertNotNull(dto.getCreateAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("No response from admin for Test1", dto.getResponse());
    }
    @Test
    void getTicketByIDButTicketNotFound() {
        when(supportTicketRepository.findById(ArgumentMatchers.<Long>any())).thenReturn(Optional.empty());
        assertThrows(TicketNotFoundException.class,()->serviceImp.getTicketByID(1002L));
    }
}