package com.dwb.auth.login.service;

import com.dwb.auth.login.dto.LoginRequest;
import com.dwb.auth.login.dto.LoginResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.role.entity.Role;
import com.dwb.security.jwt.service.JwtService;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active. Please complete email and phone verification.");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRoles());

        // Convert Set<Role> to Set<String> for the response
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return new LoginResponse(token, user.getUniqueId(), roleNames);
    }
}
