package com.symida.authorization.service.impl;

import com.symida.authorization.model.Account;
import com.symida.authorization.payload.accounts.request.CreateRequest;
import com.symida.authorization.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AccountServiceImpl implements AccountService {

    private final PasswordEncoder passwordEncoder;

    private final WebClient webClient;

    public AccountServiceImpl(PasswordEncoder passwordEncoder, WebClient.Builder webClientBuilder) {
        this.passwordEncoder = passwordEncoder;
        webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public Account register(Account account) throws RuntimeException {
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        ResponseEntity<Void> responseEntity = webClient.post()
                .uri("/accounts/save")
                .bodyValue(CreateRequest.builder()
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .password(account.getPassword())
                        .build())
                .retrieve()
                .toBodilessEntity()
                .block();
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            return account;
        } else {
            throw new RuntimeException("Failed to register account. Response code: " + (responseEntity != null ? responseEntity.getStatusCode() : "No response"));
        }
    }
}

