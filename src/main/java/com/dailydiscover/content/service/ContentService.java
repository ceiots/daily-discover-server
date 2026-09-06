package com.dailydiscover.content.service;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.content.repository.ContentRepository;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public Optional<Content> findPublishedById(Long id) {
        return contentRepository.findByIdAndStatus(id, "PUBLISHED")
                .filter(Content::isPublished);
    }

    public List<Content> getPublishedContents(int limit) {
        return contentRepository.findPublishedContents("PUBLISHED", LocalDateTime.now())
                .stream()
                .limit(limit)
                .toList();
    }

    public Page<Content> getPublishedContents(Pageable pageable) {
        return contentRepository.findPublishedContentsPaged("PUBLISHED", LocalDateTime.now(), pageable);
    }

    public List<Content> getPublishedContentsByScene(Long sceneId, int limit) {
        return contentRepository.findBySceneIdAndStatus(sceneId, "PUBLISHED", LocalDateTime.now())
                .stream()
                .limit(limit)
                .toList();
    }

    @Transactional
    public Content createContent(Content content) {
        content.setStatus("PUBLISHED");
        return contentRepository.save(content);
    }

    @Transactional
    public Content updateContent(Long id, Content content) {
        Content existing = contentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));
        existing.setTitle(content.getTitle());
        existing.setSummary(content.getSummary());
        existing.setBody(content.getBody());
        existing.setCoverImageUrl(content.getCoverImageUrl());
        existing.setContentType(content.getContentType());
        existing.setPriority(content.getPriority());
        existing.setPublishAt(content.getPublishAt());
        existing.setExpireAt(content.getExpireAt());
        return contentRepository.save(existing);
    }

    @Transactional
    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }
}