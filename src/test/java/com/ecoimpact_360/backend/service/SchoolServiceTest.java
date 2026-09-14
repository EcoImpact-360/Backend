package com.ecoimpact_360.backend.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ecoimpact_360.backend.dto.LoginRequest;
import com.ecoimpact_360.backend.dto.LoginResponse;
import com.ecoimpact_360.backend.dto.SchoolCreateRequest;
import com.ecoimpact_360.backend.exception.ConflictException;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.SchoolRepository;
import com.ecoimpact_360.backend.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
@ExtendWith(MockitoExtension.class)
class SchoolServiceTest {
    @Mock
    private SchoolRepository schoolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private SchoolService schoolService;
    private SchoolCreateRequest createRequest;
    @BeforeEach
    void setUp() {
        createRequest = new SchoolCreateRequest();
        createRequest.setName("Colegio Central");
        createRequest.setCity("Madrid");
        createRequest.setPassword("secreta123");
    }
    @Test
    void registerSchool_HashesPasswordAndSaves() {
        when(schoolRepository.existsByNameIgnoreCase("Colegio Central")).thenReturn(false);
        when(passwordEncoder.encode("secreta123")).thenReturn("hashed-pw");
        when(schoolRepository.save(any(School.class))).thenAnswer(inv -> inv.getArgument(0));
        School result = schoolService.registerSchool(createRequest);
        assertEquals("Colegio Central", result.getName());
        assertEquals("Madrid", result.getCity());
        assertEquals("hashed-pw", result.getPassword());
    }
    @Test
    void registerSchool_ThrowsConflict_WhenNameAlreadyTaken() {
        when(schoolRepository.existsByNameIgnoreCase("Colegio Central")).thenReturn(true);
        assertThrows(ConflictException.class, () -> schoolService.registerSchool(createRequest));
        verify(schoolRepository, never()).save(any());
    }
    @Test
    void login_ReturnsTokenAndSchoolInfo_WhenCredentialsMatch() {
        School school = new School();
        school.setId(5L);
        school.setName("Colegio Central");
        school.setPassword("hashed-pw");
        LoginRequest req = new LoginRequest();
        req.setName("Colegio Central");
        req.setPassword("secreta123");
        when(schoolRepository.findByNameIgnoreCase("Colegio Central")).thenReturn(Optional.of(school));
        when(passwordEncoder.matches("secreta123", "hashed-pw")).thenReturn(true);
        when(tokenService.generateToken(5L)).thenReturn("a-token");
        LoginResponse response = schoolService.login(req);
        assertEquals("a-token", response.getToken());
        assertEquals(5L, response.getSchoolId());
        assertEquals("Colegio Central", response.getSchoolName());
    }
    @Test
    void login_ThrowsUnauthorized_WhenSchoolDoesNotExist() {
        LoginRequest req = new LoginRequest();
        req.setName("No Existe");
        req.setPassword("secreta123");
        when(schoolRepository.findByNameIgnoreCase("No Existe")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> schoolService.login(req));
    }
    @Test
    void login_ThrowsUnauthorized_WhenPasswordDoesNotMatch() {
        School school = new School();
        school.setId(5L);
        school.setName("Colegio Central");
        school.setPassword("hashed-pw");
        LoginRequest req = new LoginRequest();
        req.setName("Colegio Central");
        req.setPassword("wrong-password");
        when(schoolRepository.findByNameIgnoreCase("Colegio Central")).thenReturn(Optional.of(school));
        when(passwordEncoder.matches("wrong-password", "hashed-pw")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> schoolService.login(req));
    }
}
