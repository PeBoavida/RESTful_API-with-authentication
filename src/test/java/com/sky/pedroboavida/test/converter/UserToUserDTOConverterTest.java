package com.sky.pedroboavida.test.converter;

import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UserDTO;
import com.sky.pedroboavida.test.entity.ExternalProject;
import com.sky.pedroboavida.test.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserToUserDTOConverterTest {

    @Mock
    private ExternalProjectToExternalProjectDTOConverter externalProjectConverter;

    @InjectMocks
    private UserToUserDTOConverter converter;

    private User testUser;
    private ExternalProject testProject1;
    private ExternalProject testProject2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("encodedPassword");

        testProject1 = new ExternalProject();
        testProject1.setId("project-1");
        testProject1.setName("Project 1");
        testProject1.setUser(testUser);

        testProject2 = new ExternalProject();
        testProject2.setId("project-2");
        testProject2.setName("Project 2");
        testProject2.setUser(testUser);
    }

    @Test
    void convert_Success() {
        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertNull(result.getExternalProjects()); // No projects set
    }

    @Test
    void convert_WithNullSource_ReturnsNull() {
        // Act
        UserDTO result = converter.convert(null);

        // Assert
        assertNull(result);
        verifyNoInteractions(externalProjectConverter);
    }

    @Test
    void convert_WithExternalProjects_ConvertsAllProjects() {
        // Arrange
        Set<ExternalProject> projects = new HashSet<>();
        projects.add(testProject1);
        projects.add(testProject2);
        testUser.setExternalProjects(projects);

        ExternalProjectDTO projectDTO1 = new ExternalProjectDTO();
        projectDTO1.setId("project-1");
        projectDTO1.setName("Project 1");
        projectDTO1.setUserId(1L);

        ExternalProjectDTO projectDTO2 = new ExternalProjectDTO();
        projectDTO2.setId("project-2");
        projectDTO2.setName("Project 2");
        projectDTO2.setUserId(1L);

        when(externalProjectConverter.convert(testProject1)).thenReturn(projectDTO1);
        when(externalProjectConverter.convert(testProject2)).thenReturn(projectDTO2);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        
        assertNotNull(result.getExternalProjects());
        assertEquals(2, result.getExternalProjects().size());
        assertTrue(result.getExternalProjects().contains(projectDTO1));
        assertTrue(result.getExternalProjects().contains(projectDTO2));
        
        verify(externalProjectConverter, times(2)).convert(any(ExternalProject.class));
    }

    @Test
    void convert_WithEmptyExternalProjects_ReturnsDTOWithNullProjects() {
        // Arrange
        testUser.setExternalProjects(new HashSet<>());

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertNull(result.getExternalProjects());
        verifyNoInteractions(externalProjectConverter);
    }

    @Test
    void convert_WithNullExternalProjects_ReturnsDTOWithNullProjects() {
        // Arrange
        testUser.setExternalProjects(null);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertNull(result.getExternalProjects());
        verifyNoInteractions(externalProjectConverter);
    }

    @Test
    void convert_WithNullName_ReturnsDTOWithNullName() {
        // Arrange
        testUser.setName(null);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getName());
    }

    @Test
    void convert_WithEmptyName_ReturnsDTOWithEmptyName() {
        // Arrange
        testUser.setName("");

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("", result.getName());
    }

    @Test
    void convert_WithSingleExternalProject_ConvertsProject() {
        // Arrange
        Set<ExternalProject> projects = new HashSet<>();
        projects.add(testProject1);
        testUser.setExternalProjects(projects);

        ExternalProjectDTO projectDTO = new ExternalProjectDTO();
        projectDTO.setId("project-1");
        projectDTO.setName("Project 1");
        projectDTO.setUserId(1L);

        when(externalProjectConverter.convert(testProject1)).thenReturn(projectDTO);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getExternalProjects());
        assertEquals(1, result.getExternalProjects().size());
        assertTrue(result.getExternalProjects().contains(projectDTO));
        verify(externalProjectConverter).convert(testProject1);
    }

    @Test
    void convert_WithNullExternalProjectInSet_HandlesGracefully() {
        // Arrange
        Set<ExternalProject> projects = new HashSet<>();
        projects.add(testProject1);
        projects.add(null); // Null project in set
        testUser.setExternalProjects(projects);

        ExternalProjectDTO projectDTO = new ExternalProjectDTO();
        projectDTO.setId("project-1");
        projectDTO.setName("Project 1");
        projectDTO.setUserId(1L);

        when(externalProjectConverter.convert(testProject1)).thenReturn(projectDTO);
        when(externalProjectConverter.convert(null)).thenReturn(null);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getExternalProjects());
        // Should handle null gracefully - either skip or include null
        verify(externalProjectConverter, atLeastOnce()).convert(any());
    }

    @Test
    void convert_WithLongEmail_ReturnsDTOWithLongEmail() {
        // Arrange
        String longEmail = "a".repeat(200) + "@example.com";
        testUser.setEmail(longEmail);

        // Act
        UserDTO result = converter.convert(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(longEmail, result.getEmail());
        assertEquals(1L, result.getId());
    }
}

