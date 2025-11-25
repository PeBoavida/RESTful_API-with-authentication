package com.sky.pedroboavida.test.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Email(message = "Email must be valid")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    private String email;

    @Size(min = 6, max = 129, message = "Password must be between 6 and 129 characters")
    private String password;

    @Size(max = 120, message = "Name must not exceed 120 characters")
    private String name;
}

