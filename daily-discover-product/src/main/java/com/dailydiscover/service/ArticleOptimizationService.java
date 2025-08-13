package com.dailydiscover.service;

import com.dailydiscover.model.Article;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleOptimizationService {

    // 提取文章摘要，限制在100字以内
    public String extractSummary(String content, int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 移除HTML标签
        String plainText = content.replaceAll("<[^>]*>", "");
        
        // 移除多余空白字符
        plainText = plainText.replaceAll("\\s+", " ").trim();
        
        // 限制长度并添加省略号
        if (plainText.length() > maxLength) {
            return plainText.substring(0, maxLength) + "...";
        }
        
        return plainText;
    }
    
    // 优化文章标题，使其更加吸引人
    public String optimizeTitle(String title) {
        if (title == null || title.isEmpty()) {
            return title;
        }
        
        // 如果标题太长，截断它
        if (title.length() > 30) {
            return title.substring(0, 27) + "...";
        }
        
        return title;
    }
    
    // 优化文章副标题
    public String optimizeSubtitle(String subtitle) {
        if (subtitle == null || subtitle.isEmpty()) {
            return subtitle;
        }
        
        // 如果副标题太长，截断它
        if (subtitle.length() > 50) {
            return subtitle.substring(0, 47) + "...";
        }
        
        return subtitle;
    }
    
    // 提取文章中的关键点，形成要点列表
    public List<String> extractKeyPoints(String content, int maxPoints) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        
        // 尝试从标题中提取要点
        Pattern h2Pattern = Pattern.compile("<h2[^>]*>(.*?)</h2>", Pattern.DOTALL);
        Matcher h2Matcher = h2Pattern.matcher(content);
        
        String[] keyPoints = new String[maxPoints];
        int count = 0;
        
        // 从H2标题中提取要点
        while (h2Matcher.find() && count < maxPoints) {
            String point = h2Matcher.group(1).replaceAll("<[^>]*>", "").trim();
            if (!point.isEmpty()) {
                keyPoints[count++] = point;
            }
        }
        
        // 如果从标题中提取的要点不足，尝试从段落中提取
        if (count < maxPoints) {
            Pattern pPattern = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.DOTALL);
            Matcher pMatcher = pPattern.matcher(content);
            
            while (pMatcher.find() && count < maxPoints) {
                String paragraph = pMatcher.group(1).replaceAll("<[^>]*>", "").trim();
                if (paragraph.length() > 20) {  // 只考虑较长的段落
                    String point = paragraph;
                    if (point.length() > 100) {
                        point = point.substring(0, 97) + "...";
                    }
                    keyPoints[count++] = point;
                }
            }
        }
        
        // 返回非空要点
        return Arrays.stream(keyPoints)
                .filter(p -> p != null && !p.isEmpty())
                .toList();
    }
    
    // 优化文章内容，添加小标题和分段
    public String optimizeContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        
        // 确保段落之间有足够的空间
        content = content.replaceAll("</p>\\s*<p", "</p>\n\n<p");
        
        // 确保标题与段落之间有足够的空间
        content = content.replaceAll("</h[1-6]>\\s*<p", "</h$1>\n\n<p");
        content = content.replaceAll("</p>\\s*<h[1-6]", "</p>\n\n<h$1");
        
        return content;
    }
    
    // 优化整个文章对象
    public Article optimizeArticle(Article article) {
        if (article == null) {
            return null;
        }
        
        // 优化标题和副标题
        article.setTitle(optimizeTitle(article.getTitle()));
        article.setSubtitle(optimizeSubtitle(article.getSubtitle()));
        
        // 优化内容
        article.setContent(optimizeContent(article.getContent()));
        
        return article;
    }
}