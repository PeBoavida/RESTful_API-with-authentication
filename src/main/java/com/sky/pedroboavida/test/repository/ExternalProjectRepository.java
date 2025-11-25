package com.sky.pedroboavida.test.repository;

import com.sky.pedroboavida.test.entity.ExternalProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExternalProjectRepository extends JpaRepository<ExternalProject, String> {
    List<ExternalProject> findByUserId(Long userId);
    
    @Query("SELECT ep FROM ExternalProject ep WHERE ep.id = :projectId AND ep.user.id = :userId")
    Optional<ExternalProject> findByIdAndUserId(@Param("projectId") String projectId, @Param("userId") Long userId);
    
    boolean existsByIdAndUserId(String projectId, Long userId);
}

