package com.sky.pedroboavida.test.service;

import com.sky.pedroboavida.test.converter.ExternalProjectToExternalProjectDTOConverter;
import com.sky.pedroboavida.test.model.CreateExternalProjectRequest;
import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UpdateExternalProjectRequest;
import com.sky.pedroboavida.test.entity.ExternalProject;
import com.sky.pedroboavida.test.entity.User;
import com.sky.pedroboavida.test.exception.ExternalProjectAlreadyExistsException;
import com.sky.pedroboavida.test.exception.ExternalProjectNotFoundException;
import com.sky.pedroboavida.test.exception.UserNotFoundException;
import com.sky.pedroboavida.test.repository.ExternalProjectRepository;
import com.sky.pedroboavida.test.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalProjectServiceTest {

    @Mock
    private ExternalProjectRepository externalProjectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExternalProjectToExternalProjectDTOConverter externalProjectConverter;

    @InjectMocks
    private ExternalProjectService externalProjectService;

    private User testUser;
    private ExternalProject testProject;
    private ExternalProjectDTO testProjectDTO;
    private CreateExternalProjectRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testProject = new ExternalProject();
        testProject.setId("project-1");
        testProject.setName("Test Project");
        testProject.setUser(testUser);

        testProjectDTO = new ExternalProjectDTO();
        testProjectDTO.setId("project-1");
        testProjectDTO.setName("Test Project");
        testProjectDTO.setUserId(1L);

        createRequest = new CreateExternalProjectRequest();
        createRequest.setId("project-1");
        createRequest.setName("Test Project");
    }

    @Test
    void addExternalProject_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(externalProjectRepository.existsByIdAndUserId("project-1", 1L)).thenReturn(false);
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(testProject);
        when(externalProjectConverter.convert(any(ExternalProject.class))).thenReturn(testProjectDTO);

        ExternalProjectDTO result = externalProjectService.addExternalProject(1L, createRequest);

        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(testProject.getName(), result.getName());
        verify(userRepository).existsById(1L);
        verify(externalProjectRepository).save(any(ExternalProject.class));
        verify(externalProjectConverter).convert(any(ExternalProject.class));
    }

    @Test
    void addExternalProject_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> 
            externalProjectService.addExternalProject(1L, createRequest));
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void addExternalProject_AlreadyExists_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.existsByIdAndUserId("project-1", 1L)).thenReturn(true);

        assertThrows(ExternalProjectAlreadyExistsException.class, () -> 
            externalProjectService.addExternalProject(1L, createRequest));
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void getExternalProjectsByUserId_Success() {
        List<ExternalProject> projects = Arrays.asList(testProject);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByUserId(1L)).thenReturn(projects);
        when(externalProjectConverter.convert(any(ExternalProject.class))).thenReturn(testProjectDTO);

        List<ExternalProjectDTO> result = externalProjectService.getExternalProjectsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.getId(), result.get(0).getId());
        verify(userRepository).existsById(1L);
        verify(externalProjectRepository).findByUserId(1L);
        verify(externalProjectConverter).convert(any(ExternalProject.class));
    }

    @Test
    void getExternalProjectsByUserId_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> 
            externalProjectService.getExternalProjectsByUserId(1L));
    }

    @Test
    void getExternalProject_Success() {
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.of(testProject));
        when(externalProjectConverter.convert(any(ExternalProject.class))).thenReturn(testProjectDTO);

        ExternalProjectDTO result = externalProjectService.getExternalProject("project-1", 1L);

        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        verify(externalProjectRepository).findByIdAndUserId("project-1", 1L);
        verify(externalProjectConverter).convert(any(ExternalProject.class));
    }

    @Test
    void getExternalProject_NotFound_ThrowsException() {
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.empty());

        assertThrows(ExternalProjectNotFoundException.class, () -> 
            externalProjectService.getExternalProject("project-1", 1L));
    }

    @Test
    void updateExternalProject_Success() {
        UpdateExternalProjectRequest updateRequest = new UpdateExternalProjectRequest();
        updateRequest.setName("Updated Project Name");

        ExternalProject updatedProject = new ExternalProject();
        updatedProject.setId("project-1");
        updatedProject.setName("Updated Project Name");
        updatedProject.setUser(testUser);

        ExternalProjectDTO updatedProjectDTO = new ExternalProjectDTO();
        updatedProjectDTO.setId("project-1");
        updatedProjectDTO.setName("Updated Project Name");
        updatedProjectDTO.setUserId(1L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.of(testProject));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(updatedProject);
        when(externalProjectConverter.convert(any(ExternalProject.class))).thenReturn(updatedProjectDTO);

        ExternalProjectDTO result = externalProjectService.updateExternalProject("project-1", 1L, updateRequest);

        assertNotNull(result);
        assertEquals("project-1", result.getId());
        assertEquals("Updated Project Name", result.getName());
        verify(userRepository).existsById(1L);
        verify(externalProjectRepository).findByIdAndUserId("project-1", 1L);
        verify(externalProjectRepository).save(any(ExternalProject.class));
        verify(externalProjectConverter).convert(any(ExternalProject.class));
    }

    @Test
    void updateExternalProject_UserNotFound_ThrowsException() {
        UpdateExternalProjectRequest updateRequest = new UpdateExternalProjectRequest();
        updateRequest.setName("Updated Name");

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> 
            externalProjectService.updateExternalProject("project-1", 1L, updateRequest));
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void updateExternalProject_ProjectNotFound_ThrowsException() {
        UpdateExternalProjectRequest updateRequest = new UpdateExternalProjectRequest();
        updateRequest.setName("Updated Name");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.empty());

        assertThrows(ExternalProjectNotFoundException.class, () -> 
            externalProjectService.updateExternalProject("project-1", 1L, updateRequest));
        verify(externalProjectRepository, never()).save(any());
    }

    @Test
    void updateExternalProject_WithNullName_DoesNotUpdate() {
        UpdateExternalProjectRequest updateRequest = new UpdateExternalProjectRequest();
        updateRequest.setName(null);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.of(testProject));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(testProject);
        when(externalProjectConverter.convert(any(ExternalProject.class))).thenReturn(testProjectDTO);

        ExternalProjectDTO result = externalProjectService.updateExternalProject("project-1", 1L, updateRequest);

        assertNotNull(result);
        assertEquals("project-1", result.getId());
        assertEquals("Test Project", result.getName()); // Name should remain unchanged
        verify(externalProjectRepository).save(any(ExternalProject.class));
        verify(externalProjectConverter).convert(any(ExternalProject.class));
    }

    @Test
    void deleteExternalProject_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.of(testProject));
        doNothing().when(externalProjectRepository).delete(testProject);

        externalProjectService.deleteExternalProject("project-1", 1L);

        verify(userRepository).existsById(1L);
        verify(externalProjectRepository).findByIdAndUserId("project-1", 1L);
        verify(externalProjectRepository).delete(testProject);
    }

    @Test
    void deleteExternalProject_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> 
            externalProjectService.deleteExternalProject("project-1", 1L));
        verify(externalProjectRepository, never()).delete(any());
    }

    @Test
    void deleteExternalProject_ProjectNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(externalProjectRepository.findByIdAndUserId("project-1", 1L))
                .thenReturn(Optional.empty());

        assertThrows(ExternalProjectNotFoundException.class, () -> 
            externalProjectService.deleteExternalProject("project-1", 1L));
        verify(externalProjectRepository, never()).delete(any());
    }
}

