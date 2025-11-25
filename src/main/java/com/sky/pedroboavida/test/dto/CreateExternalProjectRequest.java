package com.sky.pedroboavida.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateExternalProjectRequest {
    @NotBlank(message = "Project ID is required")
    @Size(max = 200, message = "Project ID must not exceed 200 characters")
    private String id;

    @NotBlank(message = "Project name is required")
    @Size(max = 120, message = "Project name must not exceed 120 characters")
    private String name;
}

