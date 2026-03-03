package com.taskmanager.service;

import com.taskmanager.dto.request.CreateProjectRequest;
import com.taskmanager.dto.request.UpdateProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponse create(CreateProjectRequest request);

    ProjectResponse getById(Long id);

    Page<ProjectResponse> getAll(Pageable pageable);

    ProjectResponse update(Long id, UpdateProjectRequest request);

    void delete(Long id);
}
