package com.dailydiscover.service;

import com.dailydiscover.model.Motto;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class MottoService {
    
    // 获取所有活跃箴言
    public List<Motto> getAllActiveMottos() {
        // 暂时返回固定的箴言列表
        return List.of(
            new Motto("把今天过成值得收藏的日子", "生活美学", 1),
            new Motto("慢下来，感受此刻的温度", "生活美学", 2),
            new Motto("生活的美学，藏在每一个细节里", "生活美学", 3),
            new Motto("简单，才是生活的真谛", "生活美学", 4),
            new Motto("用心感受，生活处处是风景", "生活美学", 5),
            new Motto("每一个平凡的日子，都值得被温柔以待", "生活美学", 6)
        );
    }
    
    // 获取随机箴言
    public Motto getRandomMotto() {
        List<Motto> mottos = getAllActiveMottos();
        if (mottos.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return mottos.get(random.nextInt(mottos.size()));
    }
    
    // 根据ID获取箴言
    public Motto getMottoById(Long id) {
        // 暂时返回固定数据
        List<Motto> mottos = getAllActiveMottos();
        return mottos.stream()
            .filter(motto -> motto.getId() != null && motto.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}