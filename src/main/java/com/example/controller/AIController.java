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
import org.json.JSONObject;

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
     * 移除回复中的think标签
     */
    private String removeThinkTags(String response) {
        if (response == null) return "";
        
        // 移除<think>标签及其内容
        response = response.replaceAll("<think>.*?</think>\\s*", "");
        
        return response;
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

    /**
     * SSE流式AI聊天接口，支持Markdown格式
     * 真正的逐字逐句流式输出
     */
    @GetMapping(value = "/chat/stream/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatWithSSE(@RequestParam("prompt") String prompt,
                                        @RequestParam(value = "sessionId", required = false) String sessionId,
                                        @RequestHeader(value = "Authorization", required = false) String token,
                                        @RequestHeader(value = "userId", required = false) String userIdHeader) {
        if (prompt == null || prompt.trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().data("提问内容不能为空").name("error"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }
        
        try {
            // 创建SSE发射器，设置更长的超时时间
            SseEmitter emitter = new SseEmitter(600000L); // 10分钟超时
            
            // 设置超时和完成回调
            emitter.onTimeout(() -> {
                log.warn("SSE流式聊天超时");
                try {
                    emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                    emitter.complete();
                } catch (Exception e) {
                    log.error("发送超时消息失败", e);
                }
            });
            
            emitter.onCompletion(() -> {
                log.info("SSE流式聊天完成");
            });
            
            // 尝试获取用户ID - 可能为null
            Long userId = null;
            boolean isGuestMode = false;
            
            try {
                userId = userIdExtractor.extractUserId(token, userIdHeader);
                
                // 确定是否为访客模式
                isGuestMode = (userId == null);
                log.info("用户模式: {}", isGuestMode ? "访客" : "登录用户");
                
                // 处理会话ID
                if (sessionId == null || sessionId.trim().isEmpty()) {
                    if (isGuestMode) {
                        sessionId = aiChatService.createGuestSession();
                        log.info("创建访客会话: {}", sessionId);
                    } else {
                        sessionId = aiChatService.createNewSession(userId);
                        log.info("创建用户会话: {}", sessionId);
                    }
                }
                
                // 只有在非访客模式下才保存聊天记录
                if (!isGuestMode) {
                    final String finalSessionId = sessionId;
                    aiChatService.saveChatRecord(userId, prompt, "user", finalSessionId);
                    log.info("已保存用户聊天记录");
                }
            } catch (Exception e) {
                log.warn("处理用户信息失败，将使用访客模式: {}", e.getMessage());
                isGuestMode = true;
            }
            
            // 发送模式信息
            try {
                JSONObject modeInfo = new JSONObject();
                modeInfo.put("mode", isGuestMode ? "guest" : "user");
                modeInfo.put("sessionId", sessionId);
                emitter.send(SseEmitter.event().name("info").data(modeInfo.toString()));
            } catch (Exception e) {
                log.warn("发送模式信息失败", e);
            }
            
            // 使用Ollama服务进行SSE流式聊天
            ollamaService.streamChatSSE(prompt, emitter);
            
            return emitter;
        } catch (Exception e) {
            log.error("初始化SSE流式聊天失败", e);
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().data("服务器错误: " + e.getMessage()).name("error"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
            return emitter;
        }
    }
}

