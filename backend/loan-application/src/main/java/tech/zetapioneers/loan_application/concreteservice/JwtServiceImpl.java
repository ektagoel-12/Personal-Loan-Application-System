package tech.zetapioneers.loan_application.concreteservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.services.JwtService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {

    private static final ObjectMapper M = new ObjectMapper();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64D = Base64.getUrlDecoder();

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String SECRET_KEY = "AOMWANKHEDEISTHEGRETESTPERSONINTHISWORLD";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24; // 1 day

    // Generate access token for a user
    @Override
    public String generateAccessToken(User user) {
        Map<String, String> claims = Map.of(
                "sub", user.getId().toString(),
                "name", user.getName(),
                "email", user.getEmail(),
                "exp", String.valueOf(Instant.now().toEpochMilli() + ACCESS_TOKEN_EXPIRATION)
        );
        return createToken(claims);
    }

    // Generate refresh token for a user
    @Override
    public String generateRefreshToken(User user) {
        Map<String, String> claims = Map.of(
                "sub", user.getId().toString(),
                "exp", String.valueOf(Instant.now().toEpochMilli() + REFRESH_TOKEN_EXPIRATION)
        );
        return createToken(claims);
    }

    // Core method to create JWT token from claims
    public String createToken(Map<String, String> claims) {
        try {
            String header = toB64(json(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = toB64(json(claims));
            String signature = sign(header + "." + payload);
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verify JWT and return claims if valid
    public Optional<Map<String, String>> verifyAndGet(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Optional.empty();

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            if (!sign(header + "." + payload).equals(signature)) return Optional.empty();

            Map<String, String> claims = fromJson(new String(B64D.decode(payload), StandardCharsets.UTF_8));

            // Optional: check expiration
            String expStr = claims.get("exp");
            if (expStr != null && Instant.now().toEpochMilli() > Long.parseLong(expStr)) {
                return Optional.empty(); // token expired
            }

            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Helper methods
    private static String json(Map<String, String> map) throws Exception {
        return M.writeValueAsString(map);
    }

    private static Map<String, String> fromJson(String json) throws Exception {
        return M.readValue(json, new TypeReference<Map<String, String>>() {});
    }

    private static String toB64(String s) {
        return B64.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALG);
        mac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), HMAC_ALG));
        return B64.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
