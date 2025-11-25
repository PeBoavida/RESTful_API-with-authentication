package com.sky.pedroboavida.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalProjectDTO {
    private String id;
    private String name;
    private Long userId;
}

