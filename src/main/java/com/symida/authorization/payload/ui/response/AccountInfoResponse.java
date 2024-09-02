package com.symida.authorization.payload.ui.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AccountInfoResponse {

    private UUID id;

    private String username;

    private String email;
}