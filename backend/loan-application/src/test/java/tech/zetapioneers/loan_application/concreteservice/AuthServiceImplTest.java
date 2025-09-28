package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.zetapioneers.loan_application.dto.AuthResponse;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.exceptions.UserRegistrationException;
import tech.zetapioneers.loan_application.exceptions.InvalidCredentialsException;
import tech.zetapioneers.loan_application.exceptions.UserNotFoundException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("password123");
        mockUser.setRole(Role.USER);
        mockUser.setIncome(50000.0);
        mockUser.setCreditScore(700);
        mockUser.setAadhar("123456789876");
    }

    @Test
    void testRegister_success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthResponse response = authService.register(mockUser);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("John Doe", response.getName());
        assertEquals("USER", response.getRole());
        assertEquals(50000, response.getIncome());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(700, response.getCreditScore());
        assertEquals("123456789876", response.getAadhar());
    }

    @Test
    void testRegister_failure_dueToException() {
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Encoding failed"));

        assertThrows(UserRegistrationException.class, () -> authService.register(mockUser));
    }

    @Test
    void testLogin_success() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthResponse response = authService.login(mockUser.getEmail(), mockUser.getPassword());

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("John Doe", response.getName());
        assertEquals("USER", response.getRole());
        assertEquals(50000, response.getIncome());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(700, response.getCreditScore());
        assertEquals("123456789876", response.getAadhar());
    }

    @Test
    void testLogin_invalidUser() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(mockUser.getEmail(), mockUser.getPassword()));
    }

    @Test
    void testLogin_invalidPassword() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(mockUser.getEmail(), mockUser.getPassword()));
    }
}
