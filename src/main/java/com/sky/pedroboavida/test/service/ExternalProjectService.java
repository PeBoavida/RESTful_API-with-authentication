package com.sky.pedroboavida.test.service;

import com.sky.pedroboavida.test.converter.ExternalProjectToExternalProjectDTOConverter;
import com.sky.pedroboavida.test.model.CreateExternalProjectRequest;
import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UpdateExternalProjectRequest;
import com.sky.pedroboavida.test.entity.ExternalProject;
import com.sky.pedroboavida.test.exception.ExternalProjectAlreadyExistsException;
import com.sky.pedroboavida.test.exception.ExternalProjectNotFoundException;
import com.sky.pedroboavida.test.exception.UserNotFoundException;
import com.sky.pedroboavida.test.repository.ExternalProjectRepository;
import com.sky.pedroboavida.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ExternalProjectService {

    private final ExternalProjectRepository externalProjectRepository;
    private final UserRepository userRepository;
    private final ExternalProjectToExternalProjectDTOConverter externalProjectConverter;

    public ExternalProjectDTO addExternalProject(Long userId, CreateExternalProjectRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        if (externalProjectRepository.existsByIdAndUserId(request.getId(), userId)) {
            throw new ExternalProjectAlreadyExistsException(request.getId(), userId);
        }

        ExternalProject project = new ExternalProject();
        project.setId(request.getId());
        project.setName(request.getName());
        project.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));

        ExternalProject savedProject = externalProjectRepository.save(project);
        return externalProjectConverter.convert(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ExternalProjectDTO> getExternalProjectsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<ExternalProject> projects = externalProjectRepository.findByUserId(userId);
        return projects.stream()
                .map(externalProjectConverter::convert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExternalProjectDTO getExternalProject(String projectId, Long userId) {
        ExternalProject project = externalProjectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ExternalProjectNotFoundException(projectId, userId));
        return externalProjectConverter.convert(project);
    }

    public ExternalProjectDTO updateExternalProject(String projectId, Long userId, UpdateExternalProjectRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        ExternalProject project = externalProjectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ExternalProjectNotFoundException(projectId, userId));

        if (request.getName() != null) {
            project.setName(request.getName());
        }

        ExternalProject updatedProject = externalProjectRepository.save(project);
        return externalProjectConverter.convert(updatedProject);
    }

    public void deleteExternalProject(String projectId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        ExternalProject project = externalProjectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ExternalProjectNotFoundException(projectId, userId));

        externalProjectRepository.delete(project);
    }
}

