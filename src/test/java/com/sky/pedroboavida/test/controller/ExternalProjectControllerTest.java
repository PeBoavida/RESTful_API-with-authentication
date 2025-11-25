package com.sky.pedroboavida.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.pedroboavida.test.model.CreateExternalProjectRequest;
import com.sky.pedroboavida.test.model.ExternalProjectDTO;
import com.sky.pedroboavida.test.model.UpdateExternalProjectRequest;
import com.sky.pedroboavida.test.service.ExternalProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExternalProjectController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class ExternalProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalProjectService externalProjectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addExternalProject_Success() throws Exception {
        CreateExternalProjectRequest request = new CreateExternalProjectRequest();
        request.setId("project-1");
        request.setName("Test Project");

        ExternalProjectDTO response = new ExternalProjectDTO();
        response.setId("project-1");
        response.setName("Test Project");
        response.setUserId(1L);

        when(externalProjectService.addExternalProject(eq(1L), any(CreateExternalProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/users/1/external-projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("project-1"))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(externalProjectService).addExternalProject(eq(1L), any(CreateExternalProjectRequest.class));
    }

    @Test
    void getExternalProjects_Success() throws Exception {
        ExternalProjectDTO project1 = new ExternalProjectDTO();
        project1.setId("project-1");
        project1.setName("Project 1");
        project1.setUserId(1L);

        ExternalProjectDTO project2 = new ExternalProjectDTO();
        project2.setId("project-2");
        project2.setName("Project 2");
        project2.setUserId(1L);

        List<ExternalProjectDTO> projects = Arrays.asList(project1, project2);

        when(externalProjectService.getExternalProjectsByUserId(1L)).thenReturn(projects);

        mockMvc.perform(get("/api/users/1/external-projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("project-1"))
                .andExpect(jsonPath("$[1].id").value("project-2"));

        verify(externalProjectService).getExternalProjectsByUserId(1L);
    }

    @Test
    void getExternalProject_Success() throws Exception {
        ExternalProjectDTO response = new ExternalProjectDTO();
        response.setId("project-1");
        response.setName("Test Project");
        response.setUserId(1L);

        when(externalProjectService.getExternalProject("project-1", 1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1/external-projects/project-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("project-1"))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(externalProjectService).getExternalProject("project-1", 1L);
    }

    @Test
    void updateExternalProject_Success() throws Exception {
        UpdateExternalProjectRequest request = new UpdateExternalProjectRequest();
        request.setName("Updated Project Name");

        ExternalProjectDTO response = new ExternalProjectDTO();
        response.setId("project-1");
        response.setName("Updated Project Name");
        response.setUserId(1L);

        when(externalProjectService.updateExternalProject(eq("project-1"), eq(1L), any(UpdateExternalProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/users/1/external-projects/project-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("project-1"))
                .andExpect(jsonPath("$.name").value("Updated Project Name"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(externalProjectService).updateExternalProject(eq("project-1"), eq(1L), any(UpdateExternalProjectRequest.class));
    }

    @Test
    void deleteExternalProject_Success() throws Exception {
        doNothing().when(externalProjectService).deleteExternalProject("project-1", 1L);

        mockMvc.perform(delete("/api/users/1/external-projects/project-1"))
                .andExpect(status().isNoContent());

        verify(externalProjectService).deleteExternalProject("project-1", 1L);
    }
}

