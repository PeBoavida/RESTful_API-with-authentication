package com.sky.pedroboavida.test.controller;

import com.sky.pedroboavida.test.api.ExternalProjectsApi;
import com.sky.pedroboavida.test.model.CreateExternalProjectRequest;
import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UpdateExternalProjectRequest;
import com.sky.pedroboavida.test.service.ExternalProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExternalProjectController implements ExternalProjectsApi {

    private final ExternalProjectService externalProjectService;

    @Override
    public ResponseEntity<ExternalProjectDTO> addExternalProject(Long userId, CreateExternalProjectRequest createExternalProjectRequest) {
        ExternalProjectDTO project = externalProjectService.addExternalProject(userId, createExternalProjectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @Override
    public ResponseEntity<List<ExternalProjectDTO>> getExternalProjects(Long userId) {
        List<ExternalProjectDTO> projects = externalProjectService.getExternalProjectsByUserId(userId);
        return ResponseEntity.ok(projects);
    }

    @Override
    public ResponseEntity<ExternalProjectDTO> getExternalProject(Long userId, String projectId) {
        ExternalProjectDTO project = externalProjectService.getExternalProject(projectId, userId);
        return ResponseEntity.ok(project);
    }

    @Override
    public ResponseEntity<ExternalProjectDTO> updateExternalProject(Long userId, String projectId, UpdateExternalProjectRequest updateExternalProjectRequest) {
        ExternalProjectDTO project = externalProjectService.updateExternalProject(projectId, userId, updateExternalProjectRequest);
        return ResponseEntity.ok(project);
    }

    @Override
    public ResponseEntity<Void> deleteExternalProject(Long userId, String projectId) {
        externalProjectService.deleteExternalProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}

