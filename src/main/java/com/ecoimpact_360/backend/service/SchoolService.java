package com.ecoimpact_360.backend.service;
import com.ecoimpact_360.backend.dto.LoginRequest;
import com.ecoimpact_360.backend.dto.LoginResponse;
import com.ecoimpact_360.backend.dto.SchoolCreateRequest;
import com.ecoimpact_360.backend.exception.ConflictException;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
import com.ecoimpact_360.backend.model.School;
import com.ecoimpact_360.backend.repository.SchoolRepository;
import com.ecoimpact_360.backend.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public School registerSchool(SchoolCreateRequest req) {
        if (schoolRepository.existsByNameIgnoreCase(req.getName())) {
            throw new ConflictException("Ya existe un colegio registrado con ese nombre");
        }
        School school = new School();
        school.setName(req.getName());
        school.setCity(req.getCity());
        school.setPassword(passwordEncoder.encode(req.getPassword()));
        return schoolRepository.save(school);
    }

    public LoginResponse login(LoginRequest req) {
        School school = schoolRepository.findByNameIgnoreCase(req.getName())
                .orElseThrow(() -> new UnauthorizedException("Colegio o contraseña incorrectos"));
        if (school.getPassword() == null || !passwordEncoder.matches(req.getPassword(), school.getPassword())) {
            throw new UnauthorizedException("Colegio o contraseña incorrectos");
        }
        String token = tokenService.generateToken(school.getId());
        return LoginResponse.builder()
                .token(token)
                .schoolId(school.getId())
                .schoolName(school.getName())
                .build();
    }
}
