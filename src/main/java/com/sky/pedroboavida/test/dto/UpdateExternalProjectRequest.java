package com.sky.pedroboavida.test.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExternalProjectRequest {
    @Size(max = 120, message = "Project name must not exceed 120 characters")
    private String name;
}

