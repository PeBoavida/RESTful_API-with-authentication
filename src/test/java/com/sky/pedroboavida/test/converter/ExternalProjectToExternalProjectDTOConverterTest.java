package com.sky.pedroboavida.test.converter;

import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.entity.ExternalProject;
import com.sky.pedroboavida.test.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExternalProjectToExternalProjectDTOConverterTest {

    @InjectMocks
    private ExternalProjectToExternalProjectDTOConverter converter;

    private User testUser;
    private ExternalProject testProject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testProject = new ExternalProject();
        testProject.setId("project-123");
        testProject.setName("Test Project");
        testProject.setUser(testUser);
    }

    @Test
    void convert_Success() {
        // Act
        ExternalProjectDTO result = converter.convert(testProject);

        // Assert
        assertNotNull(result);
        assertEquals("project-123", result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void convert_WithNullSource_ReturnsNull() {
        // Act
        ExternalProjectDTO result = converter.convert(null);

        // Assert
        assertNull(result);
    }

    @Test
    void convert_WithNullUser_ReturnsDTOWithNullUserId() {
        // Arrange
        ExternalProject projectWithoutUser = new ExternalProject();
        projectWithoutUser.setId("project-456");
        projectWithoutUser.setName("Project Without User");
        projectWithoutUser.setUser(null);

        // Act
        ExternalProjectDTO result = converter.convert(projectWithoutUser);

        // Assert
        assertNotNull(result);
        assertEquals("project-456", result.getId());
        assertEquals("Project Without User", result.getName());
        assertNull(result.getUserId());
    }

    @Test
    void convert_WithEmptyName_ReturnsDTOWithEmptyName() {
        // Arrange
        testProject.setName("");

        // Act
        ExternalProjectDTO result = converter.convert(testProject);

        // Assert
        assertNotNull(result);
        assertEquals("project-123", result.getId());
        assertEquals("", result.getName());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void convert_WithLongProjectId_ReturnsDTOWithLongId() {
        // Arrange
        String longId = "a".repeat(200);
        testProject.setId(longId);

        // Act
        ExternalProjectDTO result = converter.convert(testProject);

        // Assert
        assertNotNull(result);
        assertEquals(longId, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void convert_WithDifferentUser_ReturnsCorrectUserId() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(999L);
        differentUser.setEmail("different@example.com");
        testProject.setUser(differentUser);

        // Act
        ExternalProjectDTO result = converter.convert(testProject);

        // Assert
        assertNotNull(result);
        assertEquals("project-123", result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals(999L, result.getUserId());
    }
}

