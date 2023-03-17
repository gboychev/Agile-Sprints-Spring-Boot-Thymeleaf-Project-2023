package com.example.project62434.respositories;

import com.example.project62434.models.TaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
}
