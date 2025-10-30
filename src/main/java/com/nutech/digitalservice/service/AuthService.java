package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.LoginRequest;
import com.nutech.digitalservice.dto.LoginResponse;
import com.nutech.digitalservice.dto.RegistrationRequest;
import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.BalanceRepositoryCustom;
import com.nutech.digitalservice.repository.UserRepository;
import com.nutech.digitalservice.repository.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Qualifier;
import com.nutech.digitalservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    @Qualifier("userRepositoryCustomImpl")
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    @Qualifier("balanceRepositoryCustomImpl")
    private BalanceRepositoryCustom balanceRepositoryCustom;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegistrationRequest request) {
        // Check if email already exists using raw query with prepared statement
        if (userRepositoryCustom.existsByEmailWithRawQuery(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        // Hash password for security
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Insert user using raw query with prepared statement
        User user = userRepositoryCustom.insertUserWithRawQuery(
                request.getEmail(),
                hashedPassword,
                request.getFirstName(),
                request.getLastName()
        );

        // Create initial balance using raw query with prepared statement
        Balance balance = balanceRepositoryCustom.insertBalanceWithRawQuery(user, 0L);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return LoginResponse.builder()
                    .token(token)
                    .build();

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Username atau password salah");
        }
    }
}