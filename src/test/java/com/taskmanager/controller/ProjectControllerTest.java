package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.request.CreateProjectRequest;
import com.taskmanager.dto.request.UpdateProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void createProject_shouldReturn201() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Integration Test Project");
        request.setDescription("A test project");

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Integration Test Project"));
    }

    @Test
    void createProject_withBlankName_shouldReturn400() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("");

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    void getProjectById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/projects/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Project not found with id: 9999"));
    }

    @Test
    void getAllProjects_shouldReturn200WithPage() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateProject_shouldReturn200() throws Exception {
        // First create a project
        CreateProjectRequest create = new CreateProjectRequest();
        create.setName("Original");
        String body = mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        UpdateProjectRequest update = new UpdateProjectRequest();
        update.setName("Updated");

        mockMvc.perform(put("/api/v1/projects/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteProject_shouldReturn204() throws Exception {
        CreateProjectRequest create = new CreateProjectRequest();
        create.setName("To Delete");
        String body = mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/v1/projects/" + id))
            .andExpect(status().isNoContent());
    }
}
