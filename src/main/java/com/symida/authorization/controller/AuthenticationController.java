package com.symida.authorization.controller;

import com.symida.authorization.configuration.jwt.JwtUtils;
import com.symida.authorization.model.Account;
import com.symida.authorization.model.Role;
import com.symida.authorization.payload.ui.request.LoginRequest;
import com.symida.authorization.payload.ui.request.RegisterRequest;
import com.symida.authorization.payload.ui.response.AccountInfoResponse;
import com.symida.authorization.payload.ui.response.MessageResponse;
import com.symida.authorization.service.impl.AccountServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AccountServiceImpl accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var account = (Account) authentication.getPrincipal();
        var jwtHeader = jwtUtils.generateJwtHeader(account);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, jwtHeader)
                .body(
                        AccountInfoResponse.builder()
                                .id(account.getId())
                                .username(account.getUsername())
                                .email(account.getEmail())
                                .build()
                );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        var account = Account.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .password(registerRequest.getPassword())
                .build();

        accountService.register(account);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
