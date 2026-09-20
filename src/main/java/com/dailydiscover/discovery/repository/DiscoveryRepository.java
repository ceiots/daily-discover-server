package com.dailydiscover.discovery.repository;

import com.dailydiscover.discovery.domain.Discovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface DiscoveryRepository extends JpaRepository<Discovery, Long> {

    /**
     * 今日发现查询：PUBLISHED + 已到发布时间 + 未过期，priority DESC（01 文档第 16 节）
     */
    @Query("SELECT d FROM Discovery d WHERE d.status = 'PUBLISHED' " +
            "AND d.publishedAt <= :now AND (d.expiresAt IS NULL OR d.expiresAt > :now) " +
            "ORDER BY d.priority DESC, d.publishedAt DESC")
    List<Discovery> findTodayDiscoveries(@Param("now") OffsetDateTime now);
}
