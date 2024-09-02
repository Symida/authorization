package com.symida.authorization.payload.accounts.response;

import com.symida.authorization.model.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountFullInfoResponse {

    private UUID id;
    private String email;
    private Role role;
    private String username;

    private String password;
}
