package com.dailydiscover.scene.service;

import com.dailydiscover.scene.domain.Scene;
import com.dailydiscover.scene.repository.SceneRepository;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SceneService {

    private final SceneRepository sceneRepository;

    public List<Scene> getActiveScenes() {
        return sceneRepository.findByStatusOrderByDisplayOrderAsc("ACTIVE");
    }

    public Optional<Scene> findActiveById(Long id) {
        return sceneRepository.findByIdAndStatus(id, "ACTIVE");
    }

    @Transactional
    public Scene createScene(Scene scene) {
        scene.setStatus("ACTIVE");
        return sceneRepository.save(scene);
    }

    @Transactional
    public Scene updateScene(Long id, Scene scene) {
        Scene existing = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        existing.setName(scene.getName());
        existing.setDescription(scene.getDescription());
        existing.setCoverImageUrl(scene.getCoverImageUrl());
        existing.setDisplayOrder(scene.getDisplayOrder());
        existing.setStatus(scene.getStatus());
        return sceneRepository.save(existing);
    }

    @Transactional
    public void deleteScene(Long id) {
        sceneRepository.deleteById(id);
    }
}