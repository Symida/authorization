package com.symida.authorization.payload.ui.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}