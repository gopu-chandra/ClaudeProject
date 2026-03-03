package com.taskmanager.service;

import com.taskmanager.dto.request.CreateProjectRequest;
import com.taskmanager.dto.request.UpdateProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.mapper.ProjectMapper;
import com.taskmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMapper projectMapper;
    @InjectMocks private ProjectServiceImpl projectService;

    @Test
    void create_shouldReturnProjectResponse() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        Project project = new Project();
        ProjectResponse response = new ProjectResponse();
        response.setName("Test Project");

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.create(request);

        assertThat(result.getName()).isEqualTo("Test Project");
        verify(projectRepository).save(project);
    }

    @Test
    void getById_shouldReturnProject() {
        Project project = new Project();
        ProjectResponse response = new ProjectResponse();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.getById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Project not found with id: 99");
    }

    @Test
    void getAll_shouldReturnPagedProjects() {
        Project project = new Project();
        ProjectResponse response = new ProjectResponse();
        Page<Project> page = new PageImpl<>(List.of(project));

        when(projectRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(projectMapper.toResponse(project)).thenReturn(response);

        Page<ProjectResponse> result = projectService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void update_shouldUpdateAndReturnProject() {
        Project project = new Project();
        ProjectResponse response = new ProjectResponse();
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Updated");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.update(1L, request);

        assertThat(result).isNotNull();
        verify(projectMapper).updateFromRequest(request, project);
    }

    @Test
    void delete_shouldDeleteProject() {
        Project project = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.delete(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
