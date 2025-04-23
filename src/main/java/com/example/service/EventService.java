package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.EventMapper;
import com.example.model.Event;

@Service
public class EventService  {

    @Autowired
    private EventMapper eventMapper;

    public List<Event> getEventsByDate(String date) {
        List<Event> events = eventMapper.getEventsByDate(date);
        System.out.println("events:" + events);
        return sanitizeEvents(events);
    }
    
    public Event getEventById(Long id) {
        Event event = eventMapper.findById(id);
        return sanitizeEvent(event);
    }
    
    public List<Event> getAllEvents() {
        List<Event> events = eventMapper.findAll();
        return sanitizeEvents(events);
    }

    /**
     * 清洗单个事件的HTML内容，防止XSS攻击
     * @param event 需要处理的事件
     * @return 处理后的事件
     */
    private Event sanitizeEvent(Event event) {
        if (event != null && event.getDescription() != null) {
            // 使用Jsoup的白名单机制清洗HTML
            String safeHtml = Jsoup.clean(event.getDescription(), 
                    // 允许常见的HTML标签和属性，但禁止脚本和危险内容
                    Safelist.relaxed()
                        .addAttributes(":all", "class")
                        .addAttributes("img", "alt", "src")
                        .addAttributes("a", "target", "href")
                        .addAttributes("table", "border", "cellpadding", "cellspacing")
                        .addAttributes("th", "colspan", "rowspan")
                        .addAttributes("td", "colspan", "rowspan")
            );
            event.setDescription(safeHtml);
            
            // 如果事件模型没有plainDescription字段，可以在这里添加扩展属性
            // event.setPlainDescription(Jsoup.parse(event.getDescription()).text());
        }
        return event;
    }
    
    /**
     * 批量清洗事件列表中的HTML内容
     * @param events 事件列表
     * @return 处理后的事件列表
     */
    private List<Event> sanitizeEvents(List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::sanitizeEvent)
                .collect(Collectors.toList());
    }
} 