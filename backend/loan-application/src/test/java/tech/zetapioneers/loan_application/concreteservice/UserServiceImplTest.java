package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.zetapioneers.loan_application.dto.UpdateUserRequest;
import tech.zetapioneers.loan_application.entities.User;
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
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user1;
    User user2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user2 = new User();

        user1.setId(1L);
        user1.setEmail("aom@gmail.com");
        user1.setAadhar("989765749375");
        user1.setIncome(1000.0);
        user1.setCreditScore(599);
        user1.setRole(Role.USER);

        user2.setId(2L);
        user2.setEmail("aom1@gmail.com");
        user2.setAadhar("989765949375");
        user2.setIncome(1000.0);
        user2.setCreditScore(599);
        user2.setRole(Role.USER);
    }

    private void mockAuthentication(Long userId) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getAllUser() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUser();

        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
        assertEquals("aom@gmail.com", users.get(0).getEmail());
        assertEquals("aom1@gmail.com", users.get(1).getEmail());
    }

    @Test
    void getUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("No user with id 1 found", exception.getMessage());
    }

    @Test
    void updateUserById_AdminUser() {
        mockAuthentication(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("newemail@example.com");

        Optional<User> updatedUser = userService.updateUserById(1L, updateUserRequest);

        assertTrue(updatedUser.isPresent());
        assertEquals("newemail@example.com", updatedUser.get().getEmail());
    }

    @Test
    void updateUserById_SelfUpdate() {
        mockAuthentication(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("newemail@example.com");

        Optional<User> updatedUser = userService.updateUserById(1L, updateUserRequest);

        assertTrue(updatedUser.isPresent());
        assertEquals("newemail@example.com", updatedUser.get().getEmail());
    }

    @Test
    void updateUserById_NonAdminUser_Forbidden() {
        mockAuthentication(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("newemail@example.com");

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            userService.updateUserById(1L, updateUserRequest);
        });

        assertEquals("You are not authorized to update other users", exception.getMessage());
    }

    @Test
    void updateUserById_UserTryingToChangeRole() {
        mockAuthentication(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setRole(Role.ADMIN);

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            userService.updateUserById(1L, updateUserRequest);
        });

        assertEquals("You are not authorized to update the role", exception.getMessage());
    }

    @Test
    void deleteUserById_AdminUser() {
        mockAuthentication(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_SelfDelete() {
        mockAuthentication(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_NonAdminUser_Forbidden() {
        mockAuthentication(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            userService.deleteUserById(1L);
        });

        assertEquals("You are not authorized to delete other users", exception.getMessage());
    }
}
