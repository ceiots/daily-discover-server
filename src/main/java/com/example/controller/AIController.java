package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.service.OllamaService;
import com.example.service.ProductService;
import com.example.service.AiChatService;
import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.util.UserIdExtractor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserIdExtractor userIdExtractor;
    
    @Autowired
    private AiChatService aiChatService;

    @PostMapping(value = "/generate-article", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateArticle(@RequestBody String prompt) {
        return ollamaService.generateArticle(prompt);
    }

    /**
     * 智能AI聊天接口
     */
    @PostMapping("/chat")
    public CommonResult<String> chatWithAI(@RequestBody Map<String, String> requestBody,
                                           @RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            String prompt = requestBody.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return CommonResult.failed("提问内容不能为空");
            }
            
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            
            // 获取或创建会话ID
            String sessionId = requestBody.get("sessionId");
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = aiChatService.createNewSession(userId);
            }
            
            // 保存用户提问记录
            aiChatService.saveChatRecord(userId, prompt, "user", sessionId);

            // 使用Ollama服务进行聊天
            String aiResponse;
            try {
                aiResponse = ollamaService.asyncChat(prompt).get();
                
                // 保存AI回复记录
                aiChatService.saveChatRecord(userId, aiResponse, "ai", sessionId);
                
                return CommonResult.success(aiResponse);
            } catch (Exception e) {
                log.error("调用Ollama服务失败", e);
                return CommonResult.failed("本地模型服务暂时不可用，请稍后再试");
            }
        } catch (Exception e) {
            log.error("AI聊天出错", e);
            return CommonResult.failed("AI服务暂时不可用，请稍后再试");
        }
    }


    /**
     * 获取每日智能推荐商品
     */
    @GetMapping("/daily-discovery")
    public CommonResult<Map<String, Object>> getDailyDiscovery(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 获取随机商品作为每日推荐
            List<Product> randomProducts = productService.getRandomProducts();

            // 添加一些智能推荐信息
            Map<String, Object> result = new HashMap<>();
            result.put("products", randomProducts);

            // 添加智能推荐理由
            List<String> reasons = new ArrayList<>();
            for (int i = 0; i < randomProducts.size() && i < 3; i++) {
                Product product = randomProducts.get(i);
                reasons.add(generateRecommendationReason(product));
            }
            result.put("reasons", reasons);

            // 添加今日主题
            result.put("theme", getDailyTheme());
            result.put("themeDescription", getDailyThemeDescription());

            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取每日发现推荐失败", e);
            return CommonResult.failed("获取推荐失败：" + e.getMessage());
        }
    }


    /**
     * 获取快速问题列表
     */
    @GetMapping("/quick-questions")
    public CommonResult<List<String>> getQuickQuestions() {
        try {
            List<String> questions = aiChatService.getQuickQuestions();
            return CommonResult.success(questions);
        } catch (Exception e) {
            log.error("获取快速问题失败", e);
            return CommonResult.failed("获取快速问题失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI历史聊天记录
     */
    @GetMapping("/chat-history")
    public CommonResult<List<Map<String, Object>>> getChatHistory(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            
            // 获取聊天历史
            List<Map<String, Object>> history = aiChatService.getChatHistory(userId, pageNum, pageSize);
            
            return CommonResult.success(history);
        } catch (Exception e) {
            log.error("获取聊天历史失败", e);
            return CommonResult.failed("获取聊天历史失败：" + e.getMessage());
        }
    }
    
    
    /**
     * 清空聊天历史
     */
    @PostMapping("/clear-chat-history")
    public CommonResult<Boolean> clearChatHistory(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            
            // 清空聊天历史
            boolean result = aiChatService.clearChatHistory(userId);
            
            if (result) {
                return CommonResult.success(true);
            } else {
                return CommonResult.failed("清空聊天历史失败");
            }
        } catch (Exception e) {
            log.error("清空聊天历史失败", e);
            return CommonResult.failed("清空聊天历史失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据会话ID获取聊天记录
     */
    @GetMapping("/chat-session")
    public CommonResult<List<Map<String, Object>>> getChatSessionHistory(
            @RequestParam("sessionId") String sessionId) {
        try {
            List<Map<String, Object>> sessionHistory = aiChatService.getChatHistoryBySessionId(sessionId);
            return CommonResult.success(sessionHistory);
        } catch (Exception e) {
            log.error("获取会话历史失败", e);
            return CommonResult.failed("获取会话历史失败：" + e.getMessage());
        }
    }
    

    /**
     * 生成商品推荐理由
     */
    private String generateRecommendationReason(Product product) {
        String[] reasons = {
            "基于您的浏览历史，我们认为这款商品会符合您的品味",
            "这是本周最受欢迎的智能产品之一，已有多位用户好评",
            "这款产品兼具美观和实用性，是提升生活品质的不二之选",
            "精选高品质材料制作，经久耐用，值得信赖",
            "智能科技与生活完美结合，让您的日常更便捷",
            "根据您的兴趣推荐，这款产品非常适合您的使用场景",
            "与您之前查看的商品风格相似，但拥有更多创新功能",
            "众多时尚博主推荐的必备单品，质感与颜值兼具"
        };

        Random random = new Random();
        return reasons[random.nextInt(reasons.length)];
    }

    /**
     * 获取每日主题
     */
    private String getDailyTheme() {
        String[] themes = {
            "智能生活",
            "品质家居",
            "时尚穿搭",
            "健康饮食",
            "户外探险",
            "亲子时光",
            "宠物关爱"
        };

        // 根据日期选择，确保每天主题固定
        int day = java.time.LocalDate.now().getDayOfMonth();
        return themes[day % themes.length];
    }

    /**
     * 获取每日主题描述
     */
    private String getDailyThemeDescription() {
        String theme = getDailyTheme();

        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("智能生活", "科技让生活更便捷，精选高品质智能家居产品，提升您的生活质量");
        descriptions.put("品质家居", "打造舒适温馨的家，从精选家居好物开始，让家更有品味");
        descriptions.put("时尚穿搭", "流行元素与个性风格的完美融合，展现您的时尚品味");
        descriptions.put("健康饮食", "健康生活从饮食开始，精选食材与厨具，做出美味健康的佳肴");
        descriptions.put("户外探险", "拥抱自然，感受户外的魅力，必备装备让探险更安全舒适");
        descriptions.put("亲子时光", "陪伴是最好的教育，精选亲子互动产品，共度美好时光");
        descriptions.put("宠物关爱", "给毛孩子最好的关爱，从精选宠物用品开始，让它们健康快乐");

        return descriptions.getOrDefault(theme, "发现生活中的美好，精选优质商品，提升生活品质");
    }
}
