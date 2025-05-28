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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.model.ChatMessage;
import com.example.service.OllamaService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Date;

// 添加WebFlux相关导入
import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.time.Duration;

import com.example.service.ProductService;
import com.example.service.AiChatService;
import com.example.common.api.CommonResult;
import com.example.model.Product;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
     * 使用WebFlux实现的SSE流式AI聊天接口，支持Markdown格式
     * 真正的逐字逐句流式输出，使用Reactor响应式流
     */
    @GetMapping(value = "/chat/stream/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChatWithSSE(
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("提问内容不能为空")
                    .build());
        }
        
        // 创建Sink，用于向流中推送数据，使用多播模式和更小的缓冲区
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().directBestEffort();
        
        // 创建心跳事件流，每5秒发送一次心跳保持连接
        Flux<ServerSentEvent<String>> heartbeat = Flux.interval(Duration.ofSeconds(5))
                .map(i -> ServerSentEvent.<String>builder()
                        .event("heartbeat")
                        .data("ping")
                        .build());
        
        try {
            // 处理会话和用户信息
            CompletableFuture.runAsync(() -> {
                try {
                    // 尝试获取用户ID - 可能为null
                    Long userId = null;
                    boolean isGuestMode = false;
                    
                    try {
                        userId = userIdExtractor.extractUserId(token, userIdHeader);
                        
                        // 确定是否为访客模式
                        isGuestMode = (userId == null);
                        log.info("用户模式: {}", isGuestMode ? "访客" : "登录用户");
                        
                        // 处理会话ID
                        String finalSessionId = sessionId;
                        if (finalSessionId == null || finalSessionId.trim().isEmpty()) {
                            if (isGuestMode) {
                                finalSessionId = aiChatService.createGuestSession();
                                log.info("创建访客会话: {}", finalSessionId);
                            } else {
                                finalSessionId = aiChatService.createNewSession(userId);
                                log.info("创建用户会话: {}", finalSessionId);
                            }
                        }
                        
                        // 只有在非访客模式下才保存聊天记录
                        if (!isGuestMode) {
                            aiChatService.saveChatRecord(userId, prompt, "user", finalSessionId);
                            log.info("已保存用户聊天记录");
                        }
                        
                        // 发送会话信息
                        JSONObject modeInfo = new JSONObject();
                        modeInfo.put("mode", isGuestMode ? "guest" : "user");
                        modeInfo.put("sessionId", finalSessionId);
                        
                        sink.tryEmitNext(ServerSentEvent.<String>builder()
                                .event("info")
                                .data(modeInfo.toString())
                                .build());
                        
                        // 调用Ollama服务进行流式聊天，使用响应式方式
                        ollamaService.streamChatWithReactor(prompt, text -> {
                            // 每当收到新的文本块，立即发送到客户端，不积累
                            if (text != null && !text.isEmpty()) {
                                // 将文本块包装成JSON格式
                                JSONObject dataObj = new JSONObject();
                                dataObj.put("data", text);
                                
                                // 发送到流中，使用非阻塞方式
                                Sinks.EmitResult result = sink.tryEmitNext(ServerSentEvent.<String>builder()
                                        .event("message")
                                        .data(dataObj.toString())
                                        .build());
                                
                                if (result.isFailure()) {
                                    log.warn("发送数据块失败: {}", result);
                                }
                            }
                        }, error -> {
                            // 处理错误
                            log.error("流式聊天出错: {}", error.getMessage());
                            JSONObject errorObj = new JSONObject();
                            errorObj.put("error", error.getMessage());
                            
                            sink.tryEmitNext(ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data(errorObj.toString())
                                    .build());
                            
                            sink.tryEmitNext(ServerSentEvent.<String>builder()
                                    .event("complete")
                                    .data("[DONE]")
                                    .build());
                            
                            sink.tryEmitComplete();
                        }, () -> {
                            // 处理完成
                            log.info("流式聊天完成");
                            
                            // 发送完成事件
                            sink.tryEmitNext(ServerSentEvent.<String>builder()
                                    .event("complete")
                                    .data("[DONE]")
                                    .build());
                            
                            // 完成流
                            sink.tryEmitComplete();
                        });
                        
                    } catch (Exception e) {
                        log.warn("处理用户信息失败，将使用访客模式: {}", e.getMessage());
                        
                        // 发送错误信息
                        sink.tryEmitNext(ServerSentEvent.<String>builder()
                                .event("error")
                                .data("处理用户信息失败: " + e.getMessage())
                                .build());
                        
                        sink.tryEmitComplete();
                    }
                } catch (Exception e) {
                    log.error("处理SSE流失败", e);
                    sink.tryEmitError(e);
                }
            });
            
            // 合并数据流和心跳流，确保连接不会因为长时间没有数据而断开
            return Flux.merge(sink.asFlux(), heartbeat)
                    // 禁用背压缓冲，确保数据立即发送
                    .onBackpressureDrop(event -> log.warn("丢弃事件，客户端处理速度过慢"))
                    // 添加小延迟，确保事件不会堆积
                    .delayElements(Duration.ofMillis(5))
                    // 添加超时处理
                    .timeout(Duration.ofMinutes(10))
                    // 捕获超时异常
                    .onErrorResume(e -> {
                        log.error("SSE流超时或出错: {}", e.getMessage());
                        return Flux.just(ServerSentEvent.<String>builder()
                                .event("error")
                                .data("连接超时或发生错误: " + e.getMessage())
                                .build(),
                                ServerSentEvent.<String>builder()
                                .event("complete")
                                .data("[DONE]")
                                .build());
                    });
            
        } catch (Exception e) {
            log.error("初始化SSE流式聊天失败", e);
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("服务器错误: " + e.getMessage())
                    .build(),
                    ServerSentEvent.<String>builder()
                    .event("complete")
                    .data("[DONE]")
                    .build());
        }
    }

    @MessageMapping("/chat-ws")
    public void handleChat(ChatMessage message, Principal principal, 
                         SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String prompt = message.getContent();
        
        log.info("收到WebSocket聊天请求: {}, sessionId: {}", prompt, sessionId);
        
        // 每个连接创建唯一的目标
        String destination = "/topic/messages/" + sessionId;
        
        // 使用Ollama服务进行流式聊天
        ollamaService.streamChatWithWebSocket(prompt, text -> {
            // 每当收到新的文本块，立即发送到客户端
            if (text != null && !text.isEmpty()) {
                ChatMessage response = new ChatMessage();
                response.setContent(text);
                response.setType("AI");
                response.setTimestamp(new Date());
                response.setSessionId(sessionId);
                
                messagingTemplate.convertAndSend(destination, response);
            }
        }, error -> {
            // 处理错误
            log.error("WebSocket聊天出错: {}", error.getMessage());
            ChatMessage errorMsg = new ChatMessage();
            errorMsg.setContent("错误: " + error.getMessage());
            errorMsg.setType("ERROR");
            errorMsg.setTimestamp(new Date());
            errorMsg.setSessionId(sessionId);
            
            messagingTemplate.convertAndSend(destination, errorMsg);
        }, () -> {
            // 处理完成
            log.info("WebSocket聊天完成, sessionId: {}", sessionId);
            ChatMessage completeMsg = new ChatMessage();
            completeMsg.setContent("[DONE]");
            completeMsg.setType("COMPLETE");
            completeMsg.setTimestamp(new Date());
            completeMsg.setSessionId(sessionId);
            
            messagingTemplate.convertAndSend(destination, completeMsg);
        });
    }
    
    /**
     * 获取推荐话题API
     * 基于用户输入，使用Ollama生成相关推荐话题
     */
    @PostMapping("/get-suggestions")
    public CommonResult<List<Map<String, String>>> getSuggestions(@RequestBody Map<String, String> request) {
        String userInput = request.getOrDefault("userInput", "今日生活热点");
        log.info("获取推荐话题，输入: {}", userInput);
        
        try {
            // 构建提示词
            String prompt = String.format(
                "基于用户的输入：\"%s\"，生成5个相关的推荐话题，每个话题不超过10个字。" +
                "话题应该与用户输入相关，但更具体或者是延伸内容。" +
                "只返回话题列表，每行一个话题，不要有编号或其他文字。" +
                "例如，如果输入是\"健康饮食\"，可能的回复是：\n" +
                "低碳水饮食指南\n" +
                "蛋白质摄入建议\n" +
                "素食营养搭配\n" +
                "水果营养价值\n" +
                "健康零食选择", 
                userInput
            );
            
            // 调用Ollama服务获取推荐
            String response = ollamaService.generateText(prompt);
            
            // 解析响应
            List<String> topics = parseTopics(response);
            
            // 限制返回数量
            if (topics.size() > 5) {
                topics = topics.subList(0, 5);
            }
            
            // 添加图标
            List<Map<String, String>> result = new ArrayList<>();
            for (int i = 0; i < topics.size(); i++) {
                String topic = topics.get(i);
                Map<String, String> topicMap = new HashMap<>();
                topicMap.put("id", "t-" + (i + 1));
                topicMap.put("text", topic);
                topicMap.put("icon", getIconForTopic(topic));
                result.add(topicMap);
            }
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取推荐话题失败", e);
            
            // 返回默认话题
            List<Map<String, String>> defaultTopics = getDefaultTopics();
            return CommonResult.success(defaultTopics);
        }
    }
    
    /**
     * 解析Ollama返回的话题文本
     */
    private List<String> parseTopics(String response) {
        if (response == null || response.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按行分割
        String[] lines = response.split("\\r?\\n");
        
        // 过滤空行和格式化
        return Arrays.stream(lines)
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .map(line -> {
                // 移除可能的序号前缀 (如 "1. ", "- ", "• ")
                return line.replaceAll("^\\d+\\.\\s*|^[-•*]\\s*", "");
            })
            .filter(line -> line.length() <= 15) // 限制长度
            .collect(Collectors.toList());
    }
    
    /**
     * 根据话题内容选择合适的图标
     */
    private String getIconForTopic(String topic) {
        // 根据话题关键词选择图标
        if (topic.contains("健康") || topic.contains("饮食") || topic.contains("营养")) return "apple-alt";
        if (topic.contains("运动") || topic.contains("健身") || topic.contains("锻炼")) return "dumbbell";
        if (topic.contains("科技") || topic.contains("数字") || topic.contains("手机")) return "mobile-alt";
        if (topic.contains("心理") || topic.contains("压力") || topic.contains("情绪")) return "brain";
        if (topic.contains("睡眠") || topic.contains("休息")) return "moon";
        if (topic.contains("工作") || topic.contains("效率") || topic.contains("职场")) return "briefcase";
        if (topic.contains("美食") || topic.contains("烹饪") || topic.contains("菜谱")) return "utensils";
        if (topic.contains("旅行") || topic.contains("旅游") || topic.contains("出行")) return "plane";
        if (topic.contains("阅读") || topic.contains("书籍") || topic.contains("知识")) return "book";
        if (topic.contains("电影") || topic.contains("电视") || topic.contains("娱乐")) return "film";
        if (topic.contains("音乐") || topic.contains("歌曲")) return "music";
        if (topic.contains("家居") || topic.contains("装修") || topic.contains("家庭")) return "home";
        if (topic.contains("时尚") || topic.contains("穿搭") || topic.contains("服装")) return "tshirt";
        if (topic.contains("育儿") || topic.contains("孩子") || topic.contains("家庭")) return "child";
        if (topic.contains("宠物") || topic.contains("动物")) return "paw";
        if (topic.contains("金融") || topic.contains("理财") || topic.contains("投资")) return "money-bill";
        if (topic.contains("环保") || topic.contains("可持续") || topic.contains("绿色")) return "leaf";
        
        // 默认图标
        return "lightbulb";
    }
    
    /**
     * 获取默认话题列表（当API调用失败时使用）
     */
    private List<Map<String, String>> getDefaultTopics() {
        List<Map<String, String>> defaultTopics = new ArrayList<>();
        
        String[][] topics = {
            {"1", "今日热点新闻", "newspaper"},
            {"2", "健康生活指南", "heartbeat"},
            {"3", "美食推荐", "utensils"},
            {"4", "数字生活技巧", "mobile-alt"},
            {"5", "心理健康建议", "brain"}
        };
        
        for (String[] topic : topics) {
            Map<String, String> topicMap = new HashMap<>();
            topicMap.put("id", "d-" + topic[0]);
            topicMap.put("text", topic[1]);
            topicMap.put("icon", topic[2]);
            defaultTopics.add(topicMap);
        }
        
        return defaultTopics;
    }
}

