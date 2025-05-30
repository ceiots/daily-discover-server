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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.model.ChatMessage;
import com.example.service.OllamaService;
import com.example.util.UserIdExtractor;
import com.example.service.ProductService;
import com.example.service.AiChatService;
import com.example.common.api.CommonResult;
import com.example.model.Product;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;

import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

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

    // 用于存储会话信息的集合
    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    
    // 存储正在进行中的生成任务
    private final ConcurrentHashMap<String, Boolean> ongoingTasks = new ConcurrentHashMap<>();
    
    // 存储设备的最新会话ID
    private final ConcurrentHashMap<String, String> deviceLatestSession = new ConcurrentHashMap<>();

    /**
     * 获取每日智能推荐商品
     */
    @GetMapping("/daily")
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
            // 构建提示词 - 明确要求不要包含think标签
            String prompt = String.format(
                "基于用户的输入：\"%s\"，生成5个相关的推荐话题，每个话题不超过10个字。" +
                "话题应该与用户输入相关，但更具体或者是延伸内容。" +
                "只返回话题列表，每行一个话题，不要有编号或其他文字。" +
                "不要在回复中包含<think>或</think>标签。" +
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
            
            // 解析响应并过滤think标签
            List<String> topics = parseTopics(response);
            
            // 过滤掉包含think标签的话题
            topics = topics.stream()
                .filter(topic -> !topic.contains("<think>") && !topic.contains("</think>"))
                .collect(Collectors.toList());
            
            // 限制返回数量
            if (topics.size() > 5) {
                topics = topics.subList(0, 5);
            }
            
            // 如果过滤后没有话题，返回默认话题
            if (topics.isEmpty()) {
                return CommonResult.success(getDefaultTopics());
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

    /**
     * 提供WebSocket状态API，客户端可以检查服务是否可用
     */
    @GetMapping("/chat-ws/status")
    public ResponseEntity<?> getWebSocketStatus() {
        log.info("请求WebSocket状态");
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("version", "1.0");
        status.put("serverTime", System.currentTimeMillis());
        status.put("activeSessions", activeSessions.size());
        status.put("endpoints", new String[]{
            "/ai/chat-ws (WebSocket native)",
            "/ai/chat-ws (SockJS fallback)"
        });
        return ResponseEntity.ok(status);
    }

    /**
     * 使用SSE（Server-Sent Events）流式返回Ollama生成的内容
     * 前端可以直接调用此API而不需要WebSocket
     */
    @PostMapping(value = "/ollama/generate", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Flux<String>> generateWithOllama(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId) {
        
        String prompt = (String) request.getOrDefault("prompt", "");
        String model = (String) request.getOrDefault("model", ollamaService.getDefaultModel());
        // 从请求体中获取会话信息
        String requestSessionId = (String) request.getOrDefault("sessionId", sessionId);
        String requestDeviceId = (String) request.getOrDefault("deviceId", deviceId);
        
        log.info("收到Ollama生成请求: 提示词长度={}, 模型: {}, 会话ID: {}, 设备ID: {}", 
            prompt.length(), model, requestSessionId, requestDeviceId);
        
        // 检查并处理设备和会话状态
        boolean shouldCancel = checkAndUpdateSession(requestDeviceId, requestSessionId);
        if (shouldCancel) {
            log.info("发现更新的会话，取消之前的会话: {}", requestSessionId);
            // 将之前的任务标记为取消
            ongoingTasks.put(requestSessionId, false);
        }
        
        // 标记当前会话为活跃
        ongoingTasks.put(requestSessionId, true);
        
        // 使用WebFlux的Flux实现流式响应
        Flux<String> resultFlux = Flux.create(sink -> {
            try {
                // 定期检查会话状态的线程
                Thread sessionCheckThread = new Thread(() -> {
                    try {
                        while (!sink.isCancelled()) {
                            // 检查该会话是否已被取消
                            Boolean isActive = ongoingTasks.get(requestSessionId);
                            if (isActive == null || !isActive) {
                                log.info("会话已被取消，中断生成: {}", requestSessionId);
                                sink.complete();
                                break;
                            }
                            
                            // 检查是否有新的会话
                            String latestSession = deviceLatestSession.get(requestDeviceId);
                            if (latestSession != null && !latestSession.equals(requestSessionId)) {
                                log.info("发现新会话，中断旧会话: 旧={}, 新={}", requestSessionId, latestSession);
                                sink.complete();
                                break;
                            }
                            
                            Thread.sleep(1000); // 每秒检查一次
                        }
                    } catch (InterruptedException e) {
                        log.info("会话检查线程被中断");
                    }
                });
                sessionCheckThread.setDaemon(true);
                sessionCheckThread.start();
                
                // 准备请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", model);
                requestBody.put("prompt", prompt);
                requestBody.put("stream", true);  // 启用流式输出
                
                // 设置Ollama生成参数
                Map<String, Object> options = new HashMap<>();
                options.put("chunk_size", 1);     // 设置较小的分块大小，提高实时性
                options.put("temperature", 0.7);  // 温度参数，控制生成的创造性
                options.put("top_p", 0.9);        // 控制输出多样性
                requestBody.put("options", options);
                
                log.info("Ollama请求配置: model={}, stream=true, chunk_size=1, sessionId={}", 
                    model, requestSessionId);
                
                // 配置HTTP客户端，优化网络性能
                HttpClient httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.TCP_NODELAY, true)     // 禁用Nagle算法，提高小包传输速度
                    .option(ChannelOption.SO_KEEPALIVE, true)    // 保持连接活跃
                    // 优化缓冲区大小，避免延迟
                    .option(ChannelOption.SO_SNDBUF, 1) 
                    .option(ChannelOption.SO_RCVBUF, 1)
                    .responseTimeout(Duration.ofMinutes(10));     // 设置较长的超时时间
                
                log.info("开始向Ollama发送请求: {}", prompt.substring(0, Math.min(prompt.length(), 50)) + "...");
                
                // 使用自定义HTTP客户端配置创建WebClient
                ollamaService.getWebClient()
                    .mutate()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build()
                    .post()
                    .uri(ollamaService.getOllamaApiUrl() + "/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    // 关键设置：防止发射器缓冲和背压
                    .publishOn(reactor.core.scheduler.Schedulers.immediate())
                    .flatMap(chunk -> {
                        try {
                            // 再次检查会话是否仍然有效
                            Boolean isActive = ongoingTasks.get(requestSessionId);
                            if (isActive == null || !isActive) {
                                log.info("处理响应时检测到会话已取消: {}", requestSessionId);
                                return Mono.empty();
                            }
                            
                            // 解析Ollama返回的JSON
                            JSONObject jsonObject = new JSONObject(chunk);
                            String response = jsonObject.optString("response", "");
                            boolean done = jsonObject.optBoolean("done", false);
                            
                            // 只向前端传递实际的响应文本，不包含JSON结构
                            if (!response.isEmpty()) {
                                System.out.println("Ollama响应: " + response);  
                                sink.next(response);
                            }
                            
                            // 如果完成，则关闭流
                            if (done) {
                                log.info("Ollama响应已完成，流处理结束: sessionId={}", requestSessionId);
                                ongoingTasks.remove(requestSessionId); // 清理任务状态
                                sink.complete();
                            }
                            
                            return Mono.empty();
                        } catch (Exception e) {
                            log.warn("解析Ollama响应JSON出错: {}, 原始数据: {}", 
                                e.getMessage(), 
                                chunk.length() > 50 ? chunk.substring(0, 50) + "..." : chunk);
                            return Mono.empty();
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("Ollama响应流正常结束: sessionId={}", requestSessionId);
                        ongoingTasks.remove(requestSessionId); // 清理任务状态
                    })
                    .doOnError(error -> {
                        log.error("处理Ollama响应出错: {}, sessionId={}", error.getMessage(), requestSessionId, error);
                        ongoingTasks.remove(requestSessionId); // 清理任务状态
                    })
                    .doOnCancel(() -> {
                        log.info("Ollama响应流被取消: sessionId={}", requestSessionId);
                        ongoingTasks.remove(requestSessionId); // 清理任务状态
                    })
                    .subscribe();
                
            } catch (Exception e) {
                log.error("Ollama生成失败: sessionId={}", requestSessionId, e);
                ongoingTasks.remove(requestSessionId); // 清理任务状态
                sink.error(e);
            }
        }, 
        // 这里是关键改进，明确指定发射器模式为LATEST，不缓冲数据
        reactor.core.publisher.FluxSink.OverflowStrategy.LATEST);
        
        // 设置响应头，允许跨域和指定内容类型
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONNECTION, "keep-alive");
        headers.add("X-Accel-Buffering", "no"); // Nginx特殊头，禁用代理缓冲
        
        return new ResponseEntity<>(
            // 使用publishOn进一步确保响应即时处理
            resultFlux.publishOn(reactor.core.scheduler.Schedulers.immediate()), 
            headers, 
            200
        );
    }
    
    /**
     * 检查并更新会话状态
     * @return 如果需要取消之前的会话，返回true
     */
    private boolean checkAndUpdateSession(String deviceId, String sessionId) {
        if (deviceId == null || sessionId == null) {
            return false;
        }
        
        boolean shouldCancel = false;
        String previousSessionId = deviceLatestSession.get(deviceId);
        
        // 如果有之前的会话且不同于当前会话
        if (previousSessionId != null && !previousSessionId.equals(sessionId)) {
            shouldCancel = true;
            
            // 取消之前的会话
            ongoingTasks.put(previousSessionId, false);
            log.info("设备有新会话，取消旧会话: 设备={}, 旧会话={}, 新会话={}", 
                deviceId, previousSessionId, sessionId);
        }
        
        // 更新设备的最新会话
        deviceLatestSession.put(deviceId, sessionId);
        
        return shouldCancel;
    }
}

