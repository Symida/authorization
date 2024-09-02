package com.symida.authorization.service.impl;

import com.symida.authorization.model.Account;
import com.symida.authorization.payload.accounts.response.AccountFullInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final WebClient webClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var responseEntityMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts/get")
                            .queryParam("username", username)
                            .build())
                    .retrieve()
                    .toEntity(AccountFullInfoResponse.class);

            var responseEntity = responseEntityMono.block();

            if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                var accountInfoResponse = responseEntity.getBody();
                if (accountInfoResponse != null) {

                    return Account.builder()
                            .id(accountInfoResponse.getId())
                            .email(accountInfoResponse.getEmail())
                            .role(accountInfoResponse.getRole())
                            .username(accountInfoResponse.getUsername())
                            .password(accountInfoResponse.getPassword())
                            .build();
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
