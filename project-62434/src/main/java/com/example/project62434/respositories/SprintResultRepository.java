package com.example.project62434.respositories;

import com.example.project62434.models.SprintResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintResultRepository extends JpaRepository<SprintResult, Long> {
}
