package tech.zetapioneers.loan_application.concreteservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.exceptions.InvalidTokenException;
import tech.zetapioneers.loan_application.exceptions.TokenExpiredException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        mockUser = new User();
        mockUser.setId(new Random().nextLong(1));
        mockUser.setName("test tester");
        mockUser.setEmail("tester@gmail.com");
    }

    @Test
    void testGenerateAccessToken() throws Exception {
        String token = jwtService.generateAccessToken(mockUser);
        assertNotNull(token);

        Optional<Map<String, String>> claims = jwtService.verifyAndGet(token);
        assertTrue(claims.isPresent());
        assertEquals(mockUser.getId().toString(), claims.get().get("sub"));
        assertEquals(mockUser.getEmail(), claims.get().get("email"));
        assertEquals(mockUser.getName(), claims.get().get("name"));
    }

    @Test
    void testGenerateRefreshToken() throws Exception {
        String token = jwtService.generateRefreshToken(mockUser);
        assertNotNull(token);

        Optional<Map<String, String>> claims = jwtService.verifyRefreshToken(token);
        assertTrue(claims.isPresent());
        assertEquals(mockUser.getId().toString(), claims.get().get("sub"));
    }

    @Test
    void testCreateTokenAndVerify() throws Exception {
        Map<String, String> customClaims = Map.of(
                "sub", "12345",
                "exp", String.valueOf(Instant.now().toEpochMilli() + 10000)
        );
        String token = jwtService.createToken(customClaims);
        assertNotNull(token);

        Optional<Map<String, String>> claims = jwtService.verifyAndGet(token);
        assertTrue(claims.isPresent());
        assertEquals("12345", claims.get().get("sub"));
    }

    @Test
    void testVerifyAndGetWithInvalidSignature() {
        String tamperedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiIxMjM0NSIsImV4cCI6IjE2NTAwMDAwMDAwIn0."
                + "invalidsignature";

        assertThrows(InvalidTokenException.class, () -> jwtService.verifyAndGet(tamperedToken));
    }

    @Test
    void testVerifyRefreshTokenWithExpiredToken() throws Exception {
        // Create token with expired timestamp
        Map<String, String> claims = Map.of(
                "sub", "12345",
                "exp", String.valueOf(Instant.now().toEpochMilli() - 1000) // already expired
        );
        String token = jwtService.createToken(claims);

        assertThrows(TokenExpiredException.class, () -> jwtService.verifyRefreshToken(token));
    }
}
