package com.dwb.auth.service;

import com.dwb.auth.dto.LoginRequest;
import com.dwb.auth.dto.LoginResponse;
import com.dwb.security.jwt.service.JwtService;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}