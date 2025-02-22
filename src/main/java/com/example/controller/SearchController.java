// daily-discover-server/src/main/java/com/example/controller/SearchController.java
package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.dto.SearchResults;
import com.example.mapper.EventMapper;
import com.example.mapper.ProductMapper; // 导入 ProductMapper
import com.example.model.Event;
import com.example.model.Product;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private ProductMapper productMapper; // 替换为 ProductMapper

    @GetMapping("/events")
    public List<Event> searchEvents(@RequestParam String keyword) {
        return eventMapper.searchEvents(keyword); // 根据关键字搜索事件
    }

    @GetMapping("/products") // 修改路径以反映产品搜索
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productMapper.searchProducts(keyword); // 根据关键字搜索产品
    }

    @GetMapping("/all")
    public ResponseEntity<SearchResults> searchAll(@RequestParam String keyword) {
        try {
            List<Event> events = eventMapper.searchEvents(keyword);
            List<Product> products = productMapper.searchProducts(keyword); // 使用 productMapper
            
            // 返回事件和产品内容
            SearchResults results = new SearchResults(events, products); // 确保 SearchResults 构造函数正确
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            // 记录异常信息
            System.err.println("Error fetching search results: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 返回 500 错误
        }
    }
}