package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import tech.zetapioneers.loan_application.dto.LoanApplicationResponse;
import tech.zetapioneers.loan_application.dto.RepaymentScheduleDTO;
import tech.zetapioneers.loan_application.dto.UpdateUserRequest;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.LoanStatus;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.exceptions.NotAllowedException;
import tech.zetapioneers.loan_application.exceptions.UserNotFoundException;
import tech.zetapioneers.loan_application.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanApplicationServiceImpl loanApplicationService;

    @Mock
    private RepaymentScheduleServiceImpl repaymentScheduleService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup SecurityContextHolder mock
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Test users
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setRole(Role.USER);
    }

    @Test
    void testGetAllUser() {
        when(userRepository.findAll()).thenReturn(List.of(adminUser, normalUser));
        List<User> users = userService.getAllUser();
        assertEquals(2, users.size());
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testUpdateUserById_AdminCanUpdateOthers() {
        when(authentication.getPrincipal()).thenReturn("1"); // admin
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        UpdateUserRequest req = mock(UpdateUserRequest.class);
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setRole(Role.USER);
            return null;
        }).when(req).applyUpdates(any(User.class));

        Optional<User> updated = userService.updateUserById(2L, req);

        assertTrue(updated.isPresent());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserById_UserUpdatingOwnProfile() {
        when(authentication.getPrincipal()).thenReturn("2"); // normal user
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        UpdateUserRequest req = mock(UpdateUserRequest.class);
        when(req.getRole()).thenReturn(null);

        Optional<User> updated = userService.updateUserById(2L, req);

        assertTrue(updated.isPresent());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserById_UserUpdatingRole_NotAllowed() {
        when(authentication.getPrincipal()).thenReturn("2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        UpdateUserRequest req = mock(UpdateUserRequest.class);
        when(req.getRole()).thenReturn(Role.ADMIN);

        assertThrows(NotAllowedException.class, () -> userService.updateUserById(2L, req));
    }

    @Test
    void testUpdateUserById_UserUpdatingOtherUser_NotAllowed() {
        when(authentication.getPrincipal()).thenReturn("2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        UpdateUserRequest req = mock(UpdateUserRequest.class);
        when(req.getRole()).thenReturn(null);

        assertThrows(NotAllowedException.class, () -> userService.updateUserById(1L, req));
    }

    @Test
    void testDeleteUserById_AdminCanDeleteOthers() {
        when(authentication.getPrincipal()).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        userService.deleteUserById(2L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void testDeleteUserById_UserCanDeleteSelf() {
        when(authentication.getPrincipal()).thenReturn("2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        userService.deleteUserById(2L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void testDeleteUserById_UserDeletingOthers_NotAllowed() {
        when(authentication.getPrincipal()).thenReturn("2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        assertThrows(NotAllowedException.class, () -> userService.deleteUserById(3L));
    }

    @Test
    void testGetTotalPaidAmountById() {
        LoanApplicationResponse loan = new LoanApplicationResponse();
        loan.setId(101L);
        loan.setStatus(LoanStatus.APPROVED);

        RepaymentScheduleDTO paidSchedule = new RepaymentScheduleDTO();
        paidSchedule.setIsPaid(true);
        paidSchedule.setEmi(1000.0);

        when(loanApplicationService.getLoanByUser(2L)).thenReturn(List.of(loan));
        when(repaymentScheduleService.getSchedule(101L)).thenReturn(List.of(paidSchedule, paidSchedule));

        double total = userService.getTotalPaidAmountById(2L);

        assertEquals(2000.0, total);
    }

    @Test
    void testGetTotalPaidAmountById_ExceptionHandled() {
        when(loanApplicationService.getLoanByUser(2L)).thenThrow(new RuntimeException("DB error"));
        double total = userService.getTotalPaidAmountById(2L);
        assertEquals(0.0, total);
    }
}
