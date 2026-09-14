package com.ecoimpact_360.backend.security;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
class TokenServiceTest {
    private TokenService tokenService;
    @BeforeEach
    void setUp() {
        tokenService = new TokenService("test-secret-key");
    }
    @Test
    void generateAndValidateToken_RoundTripsSchoolId() {
        String token = tokenService.generateToken(42L);
        Long schoolId = tokenService.validateAndGetSchoolId(token);
        assertEquals(42L, schoolId);
    }
    @Test
    void validateAndGetSchoolId_ThrowsUnauthorized_WhenTokenIsNull() {
        assertThrows(UnauthorizedException.class, () -> tokenService.validateAndGetSchoolId(null));
    }
    @Test
    void validateAndGetSchoolId_ThrowsUnauthorized_WhenTokenHasNoSignature() {
        assertThrows(UnauthorizedException.class, () -> tokenService.validateAndGetSchoolId("nodotinhere"));
    }
    @Test
    void validateAndGetSchoolId_ThrowsUnauthorized_WhenSignatureIsTampered() {
        String token = tokenService.generateToken(42L);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        assertThrows(UnauthorizedException.class, () -> tokenService.validateAndGetSchoolId(tampered));
    }
    @Test
    void validateAndGetSchoolId_ThrowsUnauthorized_WhenSignedWithDifferentSecret() {
        String token = tokenService.generateToken(42L);
        TokenService otherService = new TokenService("a-completely-different-secret");
        assertThrows(UnauthorizedException.class, () -> otherService.validateAndGetSchoolId(token));
    }
    @Test
    void tokensForDifferentSchools_AreDifferent() {
        String tokenA = tokenService.generateToken(1L);
        String tokenB = tokenService.generateToken(2L);
        assertNotEquals(tokenA, tokenB);
    }
}
