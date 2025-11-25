package com.sky.pedroboavida.test.converter;

import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.entity.ExternalProject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ExternalProjectToExternalProjectDTOConverter implements Converter<ExternalProject, ExternalProjectDTO> {

    @Override
    public ExternalProjectDTO convert(ExternalProject source) {
        if (source == null) {
            return null;
        }

        ExternalProjectDTO dto = new ExternalProjectDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        
        if (source.getUser() != null) {
            dto.setUserId(source.getUser().getId());
        }
        
        return dto;
    }
}

