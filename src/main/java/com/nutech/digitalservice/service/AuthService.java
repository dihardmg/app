package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.LoginRequest;
import com.nutech.digitalservice.dto.LoginResponse;
import com.nutech.digitalservice.dto.RegistrationRequest;
import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.UserRepository;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        user = userRepository.save(user);

        Balance balance = Balance.builder()
                .user(user)
                .balance(0L)
                .build();

        balanceRepository.save(balance);
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