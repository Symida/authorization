package com.symida.authorization.service.impl;

import com.symida.authorization.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final WebClient webClient;

    public CustomUserDetailsService(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Mono<ResponseEntity<Account>> responseEntityMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts/get")
                            .queryParam("username", username)
                            .build())
                    .retrieve()
                    .toEntity(Account.class);

            ResponseEntity<Account> responseEntity = responseEntityMono.block();

            if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                Account account = responseEntity.getBody();
                if (account != null) {
                    return account;
                }
            }

            throw new UsernameNotFoundException("User not found with username: " + username);

        } catch (WebClientResponseException.NotFound e) {
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving user details", e);
        }
    }
}
