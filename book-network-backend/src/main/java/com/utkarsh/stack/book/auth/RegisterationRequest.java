package com.utkarsh.stack.book.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterationRequest {
    @NotEmpty(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotEmpty(message = "Last name is mandatory")
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @Email(message = "Email id is in valid")
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @Size(min = 8)
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    private String password;


}
