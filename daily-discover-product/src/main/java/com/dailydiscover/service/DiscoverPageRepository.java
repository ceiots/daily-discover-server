package com.dailydiscover.service;

import com.dailydiscover.model.DiscoverPage;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class DiscoverPageRepository {
    private final ConcurrentHashMap<Long, DiscoverPage> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    // 保存发现页面
    public DiscoverPage save(DiscoverPage discoverPage) {
        if (discoverPage.getId() == null) {
            discoverPage.setId(idGenerator.getAndIncrement());
        }
        database.put(discoverPage.getId(), discoverPage);
        return discoverPage;
    }
    
    // 根据ID查找发现页面
    public Optional<DiscoverPage> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }
    
    // 查找所有发现页面
    public List<DiscoverPage> findAll() {
        return database.values().stream().collect(Collectors.toList());
    }
    
    // 根据日期查找发现页面
    public List<DiscoverPage> findByDateInfo(String dateInfo) {
        return database.values().stream()
            .filter(page -> dateInfo.equals(page.getDateInfo()))
            .collect(Collectors.toList());
    }
    
    // 删除发现页面
    public void deleteById(Long id) {
        database.remove(id);
    }
    
    // 检查是否存在
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }
}