package com.example.project62434.respositories;

import com.example.project62434.models.ProjectResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectResultRepository extends JpaRepository<ProjectResult, Long> {
}
