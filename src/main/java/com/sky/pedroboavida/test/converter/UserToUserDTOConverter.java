package com.sky.pedroboavida.test.converter;

import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UserDTO;
import com.sky.pedroboavida.test.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserToUserDTOConverter implements Converter<User, UserDTO> {

    private final ExternalProjectToExternalProjectDTOConverter externalProjectConverter;

    @Override
    public UserDTO convert(User source) {
        if (source == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(source.getId());
        dto.setEmail(source.getEmail());
        dto.setName(source.getName());

        if (source.getExternalProjects() != null && !source.getExternalProjects().isEmpty()) {
            List<ExternalProjectDTO> projectDTOs = source.getExternalProjects().stream()
                    .map(externalProjectConverter::convert)
                    .collect(Collectors.toList());
            dto.setExternalProjects(projectDTOs);
        }

        return dto;
    }
}

