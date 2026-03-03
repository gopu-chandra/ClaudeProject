package com.taskmanager.service;

import com.taskmanager.dto.request.CreateTaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskPriority;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private TaskMapper taskMapper;
    @InjectMocks private TaskServiceImpl taskService;

    @Test
    void create_shouldReturnTaskResponse() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        Task task = new Task();
        TaskResponse response = new TaskResponse();
        response.setTitle("Test Task");

        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.create(request);

        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(task);
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Task not found with id: 99");
    }

    @Test
    void getAll_noFilters_shouldReturnAll() {
        Task task = new Task();
        TaskResponse response = new TaskResponse();
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(taskRepository).findAll(Pageable.unpaged());
    }

    @Test
    void getAll_withStatusFilter_shouldFilterByStatus() {
        Task task = new Task();
        TaskResponse response = new TaskResponse();
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findByStatus(eq(TaskStatus.TODO), any(Pageable.class))).thenReturn(page);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(TaskStatus.TODO, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(taskRepository).findByStatus(TaskStatus.TODO, Pageable.unpaged());
    }

    @Test
    void getAll_withPriorityFilter_shouldFilterByPriority() {
        Task task = new Task();
        TaskResponse response = new TaskResponse();
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findByPriority(eq(TaskPriority.HIGH), any(Pageable.class))).thenReturn(page);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(null, TaskPriority.HIGH, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(taskRepository).findByPriority(TaskPriority.HIGH, Pageable.unpaged());
    }

    @Test
    void getAll_withBothFilters_shouldFilterByStatusAndPriority() {
        Task task = new Task();
        TaskResponse response = new TaskResponse();
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findByStatusAndPriority(eq(TaskStatus.IN_PROGRESS), eq(TaskPriority.HIGH), any(Pageable.class))).thenReturn(page);
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.getAll(TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void delete_shouldDeleteTask() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
