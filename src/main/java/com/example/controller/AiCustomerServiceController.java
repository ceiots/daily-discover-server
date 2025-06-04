package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.service.OllamaService;
import com.example.util.UserIdExtractor;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AI客服控制器
 */
@Slf4j
@RestController
@RequestMapping("/ai-customer-service")
public class AiCustomerServiceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private OllamaService ollamaService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    @Autowired
    private Cache<String, String> commonAnswersCache;

    /**
     * 创建会话
     */
    @PostMapping("/create-session")
    public CommonResult<String> createSession(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody Map<String, Object> request) {
        try {
            // 获取用户ID和商品ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            Long productId = Long.parseLong(request.get("productId").toString());
            
            // 创建会话
            String sessionId = UUID.randomUUID().toString();
            
            // 插入会话记录
            jdbcTemplate.update(
                "INSERT INTO product_ai_chat_session (session_id, user_id, product_id) VALUES (?, ?, ?)",
                sessionId, userId, productId
            );
            
            return CommonResult.success(sessionId);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return CommonResult.failed("创建会话失败：" + e.getMessage());
        }
    }

    /**
     * 获取常见问题
     */
    @GetMapping("/common-questions")
    public CommonResult<List<String>> getCommonQuestions(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        
        try {
            // 查询商品特定问题、类别问题和通用问题
            List<String> questions = jdbcTemplate.queryForList(
                "SELECT question FROM product_ai_common_question " +
                "WHERE product_id = ? OR category_id = ? OR (product_id IS NULL AND category_id IS NULL) " +
                "ORDER BY priority DESC, create_time DESC LIMIT 5",
                String.class, productId, categoryId
            );
            
            return CommonResult.success(questions);
        } catch (Exception e) {
            log.error("获取常见问题失败", e);
            return CommonResult.failed("获取常见问题失败：" + e.getMessage());
        }
    }

    /**
     * 获取快速回答
     */
    @GetMapping("/quick-answer")
    public CommonResult<String> getQuickAnswer(
            @RequestParam("productId") Long productId,
            @RequestParam("query") String query) {
        
        try {
            // 先检查缓存
            String cacheKey = query + "_" + productId;
            String cachedAnswer = commonAnswersCache.getIfPresent(cacheKey);
            if (cachedAnswer != null) {
                return CommonResult.success(cachedAnswer);
            }
            
            // 简化版：使用关键词匹配
            List<Map<String, Object>> matchedQuestions = jdbcTemplate.queryForList(
                "SELECT q.id, q.question, q.answer " +
                "FROM product_ai_common_question q " +
                "WHERE (product_id = ? OR product_id IS NULL) " +
                "AND LOWER(question) LIKE LOWER(?) " +
                "ORDER BY priority DESC LIMIT 1",
                productId, "%" + query + "%"
            );
            
            if (!matchedQuestions.isEmpty()) {
                String answer = matchedQuestions.get(0).get("answer").toString();
                // 缓存结果
                commonAnswersCache.put(cacheKey, answer);
                return CommonResult.success(answer);
            }
            
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("获取快速回答失败", e);
            return CommonResult.failed("获取快速回答失败：" + e.getMessage());
        }
    }

    /**
     * 流式生成回答
     */
    @GetMapping(value = "/stream-generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerate(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("prompt") String prompt,
            @RequestParam("productId") Long productId) {

        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时

        try {
            // 保存用户消息
            jdbcTemplate.update(
                "INSERT INTO product_ai_chat_message (session_id, message, type, product_id) VALUES (?, ?, ?, ?)",
                sessionId, prompt, "user", productId);
    
            // 更新会话最后活跃时间
            jdbcTemplate.update(
                "UPDATE product_ai_chat_session SET last_active_time = NOW() WHERE session_id = ?",
                sessionId);
    
            // 异步处理AI回复
            CompletableFuture.runAsync(() -> {
                try {
                    // 获取商品信息作为上下文
                    Map<String, Object> productInfo = jdbcTemplate.queryForMap(
                        "SELECT * FROM product WHERE id = ?",
                        productId);
    
                    // 构建带有商品上下文的AI提示
                    String enhancedPrompt = buildEnhancedPrompt(prompt, productInfo);
    
                    // 调用AI服务获取流式回复
                    // 假设 ollamaService.generateStreamResponse 返回最终的完整回复字符串
                    // 并且内部通过回调处理流式片段的发送
                    String aiResponse = ollamaService.generateStreamResponse(
                        enhancedPrompt,
                        responseFragment -> {
                            try {
                                // 发送流式响应片段
                                emitter.send(SseEmitter.event()
                                    .data(Map.of("content", responseFragment))
                                    .build());
                            } catch (Exception e) {
                                log.warn("发送流式响应片段失败: {}", e.getMessage());
                            }
                        });
    
                    // 保存完整AI回复
                    jdbcTemplate.update(
                        "INSERT INTO product_ai_chat_message (session_id, message, type, product_id) VALUES (?, ?, ?, ?)",
                        sessionId, aiResponse, "ai", productId);
    
                    emitter.complete();
                } catch (Exception e) {
                    log.error("生成AI回复失败", e);
                    emitter.completeWithError(e);
                }
            });
        } catch (Exception e) {
            log.error("处理SSE请求失败", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 构建增强提示
     */
    private String buildEnhancedPrompt(String userPrompt, Map<String, Object> productInfo) {
        // 构建包含商品信息的增强提示
        return String.format(
            "您是一个专业的电商AI客服助手。以下是您正在回答问题的商品信息：\n" +
            "商品名称：%s\n" +
            "商品价格：¥%s\n" +
            "商品类别：%s\n" +
            "主要特点：%s\n\n" +
            "用户问题：%s\n\n" +
            "请根据以上商品信息，专业地回答用户问题。如果您不确定答案，请诚实地告知用户并建议联系人工客服。",
            productInfo.get("title"),
            productInfo.get("price"),
            productInfo.get("category_name"), // 确保product表中有category_name字段或通过JOIN获取
            productInfo.get("description"),
            userPrompt);
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/chat-history")
    public CommonResult<List<Map<String, Object>>> getChatHistory(
            @RequestParam("sessionId") String sessionId) {
        try {
            List<Map<String, Object>> messages = jdbcTemplate.queryForList(
                "SELECT id, message, type, create_time AS timestamp " +
                "FROM product_ai_chat_message " +
                "WHERE session_id = ? " +
                "ORDER BY create_time ASC",
                sessionId
            );
            
            return CommonResult.success(messages);
        } catch (Exception e) {
            log.error("获取聊天历史失败", e);
            return CommonResult.failed("获取聊天历史失败：" + e.getMessage());
        }
    }

    /**
     * 保存聊天消息
     */
    @PostMapping("/save-message")
    public CommonResult<Boolean> saveMessage(@RequestBody Map<String, Object> request) {
        try {
            String sessionId = (String) request.get("sessionId");
            String message = (String) request.get("message");
            String type = (String) request.get("type");
            Long productId = Long.parseLong(request.get("productId").toString());
            
            jdbcTemplate.update(
                "INSERT INTO product_ai_chat_message (session_id, message, type, product_id) VALUES (?, ?, ?, ?)",
                sessionId, message, type, productId
            );
            
            return CommonResult.success(true);
        } catch (Exception e) {
            log.error("保存聊天消息失败", e);
            return CommonResult.failed("保存聊天消息失败：" + e.getMessage());
        }
    }

    /**
     * 缓存配置
     */
    @Bean
    public Cache<String, String> commonAnswersCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();
    }

    /**
     * 获取答案（多级策略）- 这是一个示例性的同步方法，实际应更多依赖异步和流式处理
     */
    public String getAnswer(String question, Long productId) {
        // 1. 检查精确匹配缓存
        String cachedAnswer = commonAnswersCache.getIfPresent(question + "_" + productId);
        if (cachedAnswer != null) {
            log.info("Cache hit for question: {} productId: {}", question, productId);
            return cachedAnswer;
        }

        // 2. 检查语义匹配 (预设的常见问题库)
        String semanticAnswer = findSimilarQuestionAnswer(question, productId);
        if (semanticAnswer != null) {
            log.info("Semantic match for question: {} productId: {}", question, productId);
            commonAnswersCache.put(question + "_" + productId, semanticAnswer); // 缓存结果
            return semanticAnswer;
        }

        // 3. 检查规则引擎
        String ruleBasedAnswer = getRuleBasedAnswer(question, productId);
        if (ruleBasedAnswer != null) {
            log.info("Rule-based match for question: {} productId: {}", question, productId);
            commonAnswersCache.put(question + "_" + productId, ruleBasedAnswer); // 缓存结果
            return ruleBasedAnswer;
        }

        // 4. 调用AI生成 (同步阻塞方式，仅作示例，生产环境推荐异步或流式)
        log.info("Fallback to AI generation for question: {} productId: {}", question, productId);
        Map<String, Object> context = getProductContext(productId);
        if (context == null || context.isEmpty()) {
            log.warn("Product context not found for productId: {}", productId);
            return "抱歉，暂时无法获取该商品的信息，请稍后再试。";
        }
        String aiGeneratedAnswer = ollamaService.generate(question, buildEnhancedPrompt(question, context)); // 假设ollamaService有同步的generate方法
        if (aiGeneratedAnswer != null && !aiGeneratedAnswer.isEmpty()){
             commonAnswersCache.put(question + "_" + productId, aiGeneratedAnswer); // 缓存结果
        }
        return aiGeneratedAnswer;
    }

    /**
     * 查找相似问题答案
     */
    private String findSimilarQuestionAnswer(String question, Long productId) {
        try {
            // 简化版实现：关键词匹配
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT answer FROM product_ai_common_question " +
                "WHERE (product_id = ? OR product_id IS NULL) " +
                "AND LOWER(question) LIKE LOWER(?) " +
                "ORDER BY priority DESC LIMIT 1",
                productId, "%" + question + "%"
            );
            
            if (!results.isEmpty()) {
                return (String) results.get(0).get("answer");
            }
            return null;
        } catch (Exception e) {
            log.error("查找相似问题失败", e);
            return null;
        }
    }

    /**
     * 获取基于规则的答案
     */
    private String getRuleBasedAnswer(String question, Long productId) {
        // 简化规则引擎实现
        // 这里可以根据关键词匹配一些固定规则
        String lowerCaseQuestion = question.toLowerCase();
        
        // 例如处理关于发货时间的问题
        if (lowerCaseQuestion.contains("发货") || 
            lowerCaseQuestion.contains("配送") || 
            lowerCaseQuestion.contains("送达")) {
            return "我们通常在下单后24小时内发货，快递一般需要2-3天送达。具体配送时效可能因地区而异。";
        }
        
        // 处理关于退款的问题
        if (lowerCaseQuestion.contains("退款") || 
            lowerCaseQuestion.contains("退货") || 
            lowerCaseQuestion.contains("换货")) {
            return "本商品支持7天无理由退换货，如需退换货请在收到商品后7天内联系客服。请确保商品完好，不影响二次销售。";
        }
        
        return null;
    }

    /**
     * 获取商品上下文
     */
    private Map<String, Object> getProductContext(Long productId) {
        try {
            // 确保查询必要的字段，例如 category_name
            // 如果 category_name 不在 product 表中，需要进行 JOIN 操作
            return jdbcTemplate.queryForMap(
                "SELECT p.title, p.price, p.description, c.name as category_name " +
                "FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                "WHERE p.id = ?", 
                productId
            );
        } catch (Exception e) {
            log.error("获取商品上下文失败 for productId: {}", productId, e);
            // 返回空的上下文，避免空指针异常
            return new HashMap<>();
        }
    }

    /**
     * 异步生成答案
     */
    @Async
    public CompletableFuture<String> generateAnswerAsync(String question, Long productId) {
        try {
            Map<String, Object> context = getProductContext(productId);
            if (context == null || context.isEmpty()) {
                 log.warn("Async: Product context not found for productId: {}", productId);
                 return CompletableFuture.completedFuture("抱歉，暂时无法获取该商品的信息，请稍后再试。");
            }
            String answer = ollamaService.generate(question, buildEnhancedPrompt(question, context)); // 假设ollamaService有同步的generate方法
            return CompletableFuture.completedFuture(answer);
        } catch (Exception e) {
            log.error("异步生成答案失败", e);
            return CompletableFuture.completedFuture("抱歉，系统暂时无法回答您的问题，请稍后再试。");
        }
    }
}
