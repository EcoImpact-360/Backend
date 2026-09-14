package com.ecoimpact_360.backend.security;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class TokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long EXPIRATION_MS = 24L * 60 * 60 * 1000;

    private final String secret;

    public TokenService(@Value("${app.token.secret}") String secret) {
        this.secret = secret;
    }

    public String generateToken(Long schoolId) {
        long expiresAt = System.currentTimeMillis() + EXPIRATION_MS;
        String payload = schoolId + ":" + expiresAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    public Long validateAndGetSchoolId(String token) {
        if (token == null || !token.contains(".")) {
            throw new UnauthorizedException("Token inválido");
        }
        String[] parts = token.split("\\.", 2);
        String encodedPayload = parts[0];
        String signature = parts[1];
        String expectedSignature = sign(encodedPayload);
        if (!expectedSignature.equals(signature)) {
            throw new UnauthorizedException("Token inválido");
        }
        String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
        String[] payloadParts = payload.split(":", 2);
        if (payloadParts.length != 2) {
            throw new UnauthorizedException("Token inválido");
        }
        long expiresAt = Long.parseLong(payloadParts[1]);
        if (System.currentTimeMillis() > expiresAt) {
            throw new UnauthorizedException("La sesión ha expirado, vuelve a iniciar sesión");
        }
        return Long.parseLong(payloadParts[0]);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}
