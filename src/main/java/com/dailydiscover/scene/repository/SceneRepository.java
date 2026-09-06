package com.dailydiscover.scene.repository;

import com.dailydiscover.scene.domain.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {

    Optional<Scene> findByIdAndStatus(Long id, String status);

    @Query("SELECT s FROM Scene s WHERE s.status = :status ORDER BY s.displayOrder ASC, s.id ASC")
    List<Scene> findByStatusOrderByDisplayOrderAsc(@Param("status") String status);

    List<Scene> findByStatusOrderByDisplayOrderAscIdAsc(String status);
}