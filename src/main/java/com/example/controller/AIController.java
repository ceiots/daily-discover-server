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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.service.OllamaService;
import com.example.service.ProductService;
import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.util.UserIdExtractor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    @PostMapping(value = "/generate-article", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateArticle(@RequestBody String prompt) {
        return ollamaService.generateArticle(prompt);
    }

    /**
     * 智能AI聊天接口
     */
    @PostMapping("/chat")
    public CommonResult<String> chatWithAI(@RequestBody Map<String, String> requestBody) {
        try {
            String prompt = requestBody.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return CommonResult.failed("提问内容不能为空");
            }

            // 特殊处理每日发现相关的问题
            if (prompt.contains("推荐") || prompt.contains("发现") || prompt.contains("商品") ||
                prompt.contains("购物") || prompt.contains("好物")) {
                return CommonResult.success("我为您精选了今日好物，您可以在\"每日发现\"区域查看更多推荐商品。这些商品都是根据最新趋势和品质精选的。");
            }

            // 特殊处理询问天气的情况
            if (prompt.contains("天气") || prompt.contains("气温") || prompt.contains("下雨")) {
                return CommonResult.success("今天天气晴朗，气温在18-25°C之间，非常适合户外活动。建议您适当增减衣物，注意防晒。");
            }

            // 特殊处理询问日程的情况
            if (prompt.contains("日程") || prompt.contains("安排") || prompt.contains("计划") ||
                prompt.contains("会议") || prompt.contains("待办")) {
                return CommonResult.success("您今天有3个重要事项：上午10点产品评审会议，下午2点团队设计复盘，晚上6点健身预约。请合理安排时间。");
            }

            // 特殊处理询问健康建议的情况
            if (prompt.contains("健康") || prompt.contains("锻炼") || prompt.contains("饮食") ||
                prompt.contains("建议") || prompt.contains("生活")) {
                return CommonResult.success("建议您今天多喝水，保持8小时睡眠，工作1小时后适当起身活动，晚餐清淡为宜，避免久坐对颈椎和腰椎的伤害。");
            }

            // 创建SSE流进行处理
            SseEmitter emitter = ollamaService.generateArticle(prompt);

            // 由于SSE是异步的，我们这里简化处理，返回一个基本回复
            String aiResponse = "我正在思考您的问题：" +
                               prompt.substring(0, Math.min(prompt.length(), 20)) +
                               (prompt.length() > 20 ? "..." : "") +
                               "。根据我的理解，您可能对今日的精选商品感兴趣，请查看每日发现区域获取更多信息。";

            return CommonResult.success(aiResponse);
        } catch (Exception e) {
            log.error("AI聊天出错", e);
            return CommonResult.failed("AI服务暂时不可用，请稍后再试");
        }
    }

    /**
     * 处理图片识别请求
     */
    @PostMapping("/image-recognition")
    public CommonResult<Map<String, Object>> processImage(
            @RequestParam("image") MultipartFile image,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 实际项目中，这里应该集成图像识别服务
            // 这里提供模拟响应
            Map<String, Object> result = new HashMap<>();

            // 获取图片类型
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return CommonResult.failed("请上传有效的图片文件");
            }

            // 模拟识别结果
            result.put("message", "图片识别成功");

            List<Map<String, Object>> recognizedObjects = new ArrayList<>();
            Map<String, Object> object1 = new HashMap<>();
            object1.put("type", "product");
            object1.put("name", "智能手表");
            object1.put("confidence", 0.92);
            object1.put("keywords", Arrays.asList("电子设备", "可穿戴", "智能", "手表"));

            recognizedObjects.add(object1);
            result.put("objects", recognizedObjects);

            // 返回相关商品推荐
            List<Product> relatedProducts = productService.getRandomProducts().subList(0, 2);
            result.put("relatedProducts", relatedProducts);

            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("图片识别出错", e);
            return CommonResult.failed("图片处理失败：" + e.getMessage());
        }
    }

    /**
     * 获取与用户相似的用户推荐
     */
    @GetMapping("/similar-users")
    public CommonResult<List<Map<String, Object>>> getSimilarUsers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 构建相似用户数据
            List<Map<String, Object>> similarUsers = new ArrayList<>();

            // 用户1
            Map<String, Object> user1 = new HashMap<>();
            user1.put("id", 1);
            user1.put("nickname", "用户8752");
            user1.put("avatar", "https://ai-public.mastergo.com/ai/img_res/4f8bc3de41eea6ab8fafa43cc15eddb9.jpg");
            user1.put("similarity", 92);

            List<Map<String, Object>> user1Products = new ArrayList<>();
            Map<String, Object> product1 = new HashMap<>();
            product1.put("id", 101);
            product1.put("title", "超薄笔记本电脑");
            product1.put("imageUrl", "https://ai-public.mastergo.com/ai/img_res/0f04ade1d9d0b41e6fa3d64c3f0eb4e1.jpg");
            user1Products.add(product1);

            Map<String, Object> product2 = new HashMap<>();
            product2.put("id", 102);
            product2.put("title", "无线耳机");
            product2.put("imageUrl", "https://ai-public.mastergo.com/ai/img_res/54c2f01bbff2fe7f2b0d3c5fe9a3c16e.jpg");
            user1Products.add(product2);

            user1.put("favoriteProducts", user1Products);
            similarUsers.add(user1);

            // 用户2
            Map<String, Object> user2 = new HashMap<>();
            user2.put("id", 2);
            user2.put("nickname", "用户1354");
            user2.put("avatar", "https://ai-public.mastergo.com/ai/img_res/6a0d8e9e3f5eaaf9c7c88858bf59f584.jpg");
            user2.put("similarity", 85);

            List<Map<String, Object>> user2Products = new ArrayList<>();
            Map<String, Object> product3 = new HashMap<>();
            product3.put("id", 103);
            product3.put("title", "智能手表");
            product3.put("imageUrl", "https://ai-public.mastergo.com/ai/img_res/2fb03d0eead9b42769cbdb5a9c3f0e28.jpg");
            user2Products.add(product3);

            Map<String, Object> product4 = new HashMap<>();
            product4.put("id", 104);
            product4.put("title", "阅读灯");
            product4.put("imageUrl", "https://ai-public.mastergo.com/ai/img_res/a85bda61a4d8a52a20795a8e9b7f760a.jpg");
            user2Products.add(product4);

            user2.put("favoriteProducts", user2Products);
            similarUsers.add(user2);

            return CommonResult.success(similarUsers);
        } catch (Exception e) {
            log.error("获取相似用户失败", e);
            return CommonResult.failed("获取相似用户失败：" + e.getMessage());
        }
    }

    /**
     * 场景化推荐接口
     */
    @GetMapping("/scene-recommendations")
    public CommonResult<Map<String, Object>> getSceneRecommendations(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            String timeScene;

            // 根据时间段确定场景
            if (hour >= 5 && hour < 9) {
                timeScene = "早晨";
            } else if (hour >= 9 && hour < 12) {
                timeScene = "上午";
            } else if (hour >= 12 && hour < 14) {
                timeScene = "午休";
            } else if (hour >= 14 && hour < 18) {
                timeScene = "下午";
            } else if (hour >= 18 && hour < 22) {
                timeScene = "晚上";
            } else {
                timeScene = "深夜";
            }

            // 模拟天气数据（实际项目中应调用天气API）
            String[] weatherTypes = {"晴朗", "多云", "阴天", "小雨", "雷阵雨"};
            String weather = weatherTypes[new Random().nextInt(weatherTypes.length)];

            // 构建场景推荐数据
            Map<String, Object> result = new HashMap<>();
            result.put("scene", timeScene);
            result.put("weather", weather);
            result.put("location", "北京市");
            result.put("sceneText", String.format("%s好，当前%s天气%s", timeScene, "北京市", weather));

            // 根据场景和天气获取推荐商品
            List<Map<String, Object>> sceneProducts = new ArrayList<>();

            if ("晴朗".equals(weather)) {
                if ("早晨".equals(timeScene)) {
                    addSceneProduct(sceneProducts, 201, "智能咖啡机", "https://ai-public.mastergo.com/ai/img_res/aa3b7a0e9cfd7a3ef8c0c0a2bb7f7fc7.jpg", "早晨来一杯香醇的咖啡，开启美好一天");
                    addSceneProduct(sceneProducts, 202, "早餐机", "https://ai-public.mastergo.com/ai/img_res/cd2290d15ca18a83d16a5acfe5e26bd0.jpg", "三分钟搞定营养早餐");
                } else if ("晚上".equals(timeScene)) {
                    addSceneProduct(sceneProducts, 203, "智能台灯", "https://ai-public.mastergo.com/ai/img_res/63f98702070617620481ac6ea8dff875.jpg", "舒适护眼，让夜晚阅读更惬意");
                    addSceneProduct(sceneProducts, 204, "香薰机", "https://ai-public.mastergo.com/ai/img_res/5eb74c3a4ab09de7c071ae1f6a2783b8.jpg", "舒缓放松，改善睡眠质量");
                }
            } else if ("小雨".equals(weather) || "雷阵雨".equals(weather)) {
                addSceneProduct(sceneProducts, 205, "折叠雨伞", "https://ai-public.mastergo.com/ai/img_res/7a3d10b8e94339a52d13d34ffa8f51cc.jpg", "轻便易携，防风防雨");
                addSceneProduct(sceneProducts, 206, "防水鞋套", "https://ai-public.mastergo.com/ai/img_res/e4cde5ea4bb6071a2fbdb7a5242bd4a7.jpg", "保持鞋子干爽，雨天出行好帮手");
            }

            result.put("products", sceneProducts);

            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取场景化推荐失败", e);
            return CommonResult.failed("获取场景化推荐失败：" + e.getMessage());
        }
    }

    /**
     * 辅助方法：添加场景商品
     */
    private void addSceneProduct(List<Map<String, Object>> products, long id, String title, String imageUrl, String description) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("title", title);
        product.put("imageUrl", imageUrl);
        product.put("description", description);
        product.put("price", new Random().nextInt(900) + 100);
        products.add(product);
    }

    /**
     * 生成分享卡片接口
     */
    @PostMapping("/generate-share-card")
    public CommonResult<Map<String, Object>> generateShareCard(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 获取请求中的产品ID
            Long productId = ((Number) requestBody.get("productId")).longValue();
            String comment = (String) requestBody.get("comment");

            // 查询产品信息
            // 实际项目中应该从数据库中获取产品信息
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", productId);
            productInfo.put("title", "智能商品");
            productInfo.put("imageUrl", "https://ai-public.mastergo.com/ai/img_res/478a3d09562a8d8b1689ea48cf980c23.jpg");
            productInfo.put("price", 199.00);

            // 生成分享卡片数据
            Map<String, Object> shareCardData = new HashMap<>();
            shareCardData.put("productInfo", productInfo);
            shareCardData.put("shareTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            shareCardData.put("comment", comment);
            shareCardData.put("shareUrl", "https://example.com/share/" + productId);
            shareCardData.put("qrCode", "https://ai-public.mastergo.com/ai/img_res/qrcode_example.png");

            // 模拟生成分享卡片
            Map<String, Object> result = new HashMap<>();
            result.put("shareCardId", new Random().nextInt(10000));
            result.put("shareCardData", shareCardData);
            result.put("shareImageUrl", "https://ai-public.mastergo.com/ai/img_res/share_card_example.jpg");

            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("生成分享卡片失败", e);
            return CommonResult.failed("生成分享卡片失败：" + e.getMessage());
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
     * 获取用户画像数据
     */
    @GetMapping("/user-profile")
    public CommonResult<Map<String, Object>> getUserProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);

            // 构建用户画像数据
            Map<String, Object> userProfile = new HashMap<>();

            // 兴趣雷达图数据
            Map<String, Integer> interestRadar = new HashMap<>();
            interestRadar.put("科技", 85);
            interestRadar.put("时尚", 65);
            interestRadar.put("家居", 75);
            interestRadar.put("户外", 45);
            interestRadar.put("美食", 90);
            interestRadar.put("健康", 70);
            userProfile.put("interestRadar", interestRadar);

            // 消费倾向数据
            Map<String, Object> consumptionTrend = new HashMap<>();
            consumptionTrend.put("价格敏感度", 60);
            consumptionTrend.put("品牌偏好度", 75);
            consumptionTrend.put("新品尝鲜度", 85);
            consumptionTrend.put("促销响应度", 70);
            userProfile.put("consumptionTrend", consumptionTrend);

            // 推荐准确率数据
            List<Map<String, Object>> accuracyTrend = new ArrayList<>();
            String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
            int[] rates = {65, 70, 75, 78, 82, 88};
            for (int i = 0; i < months.length; i++) {
                Map<String, Object> point = new HashMap<>();
                point.put("month", months[i]);
                point.put("rate", rates[i]);
                accuracyTrend.add(point);
            }
            userProfile.put("accuracyTrend", accuracyTrend);

            return CommonResult.success(userProfile);
        } catch (Exception e) {
            log.error("获取用户画像失败", e);
            return CommonResult.failed("获取用户画像失败：" + e.getMessage());
        }
    }

    /**
     * 获取个性化体验功能入口数据
     */
    @GetMapping("/personalized-features")
    public CommonResult<List<Map<String, Object>>> getPersonalizedFeatures() {
        try {
            List<Map<String, Object>> features = new ArrayList<>();

            // 浏览历史
            Map<String, Object> history = new HashMap<>();
            history.put("id", "history");
            history.put("title", "浏览历史");
            history.put("description", "查看您最近的浏览记录");
            history.put("icon", "history");
            history.put("color", "blue");
            features.add(history);

            // 收藏夹
            Map<String, Object> favorites = new HashMap<>();
            favorites.put("id", "favorites");
            favorites.put("title", "收藏夹");
            favorites.put("description", "查看您收藏的商品");
            favorites.put("icon", "heart");
            favorites.put("color", "green");
            features.add(favorites);

            // 优惠活动
            Map<String, Object> promotions = new HashMap<>();
            promotions.put("id", "promotions");
            promotions.put("title", "优惠活动");
            promotions.put("description", "发现限时特价商品");
            promotions.put("icon", "tags");
            promotions.put("color", "purple");
            features.add(promotions);

            // 热门榜单
            Map<String, Object> hotItems = new HashMap<>();
            hotItems.put("id", "hotItems");
            hotItems.put("title", "热门榜单");
            hotItems.put("description", "了解最热门的商品");
            hotItems.put("icon", "fire");
            hotItems.put("color", "red");
            features.add(hotItems);

            // 智能搭配
            Map<String, Object> smartMatch = new HashMap<>();
            smartMatch.put("id", "smartMatch");
            smartMatch.put("title", "智能搭配");
            smartMatch.put("description", "AI智能推荐商品搭配");
            smartMatch.put("icon", "magic");
            smartMatch.put("color", "amber");
            features.add(smartMatch);

            // AR体验
            Map<String, Object> arExperience = new HashMap<>();
            arExperience.put("id", "arExperience");
            arExperience.put("title", "AR体验");
            arExperience.put("description", "虚拟试用和场景模拟");
            arExperience.put("icon", "vr-cardboard");
            arExperience.put("color", "teal");
            features.add(arExperience);

            return CommonResult.success(features);
        } catch (Exception e) {
            log.error("获取个性化功能入口失败", e);
            return CommonResult.failed("获取功能入口失败：" + e.getMessage());
        }
    }

    /**
     * 获取快捷问题列表
     */
    @GetMapping("/quick-questions")
    public CommonResult<List<String>> getQuickQuestions() {
        try {
            List<String> questions = Arrays.asList(
                "今日有什么好物推荐？",
                "这周最热门的智能产品是什么？",
                "有哪些提高生活品质的好物？",
                "给我推荐一些厨房用品",
                "有适合送礼的商品吗？",
                "夏季穿搭有什么建议？"
            );
            return CommonResult.success(questions);
        } catch (Exception e) {
            log.error("获取快捷问题失败", e);
            return CommonResult.failed("获取快捷问题失败：" + e.getMessage());
        }
    }

    /**
     * 获取互动游戏数据
     */
    @GetMapping("/interactive-games")
    public CommonResult<List<Map<String, Object>>> getInteractiveGames() {
        try {
            List<Map<String, Object>> games = new ArrayList<>();

            // 商品知识问答
            Map<String, Object> quizGame = new HashMap<>();
            quizGame.put("id", "productQuiz");
            quizGame.put("title", "商品知识问答");
            quizGame.put("description", "测试您对商品的了解程度");
            quizGame.put("icon", "question-circle");
            quizGame.put("playerCount", 1243);
            games.add(quizGame);

            // 风格猜猜猜
            Map<String, Object> styleGuess = new HashMap<>();
            styleGuess.put("id", "styleGuess");
            styleGuess.put("title", "风格猜猜猜");
            styleGuess.put("description", "猜测不同商品的风格");
            styleGuess.put("icon", "palette");
            styleGuess.put("playerCount", 987);
            games.add(styleGuess);

            // 价格竞猜
            Map<String, Object> priceGuess = new HashMap<>();
            priceGuess.put("id", "priceGuess");
            priceGuess.put("title", "价格竞猜");
            priceGuess.put("description", "猜测商品的实际价格");
            priceGuess.put("icon", "money-bill");
            priceGuess.put("playerCount", 1576);
            games.add(priceGuess);

            return CommonResult.success(games);
        } catch (Exception e) {
            log.error("获取互动游戏失败", e);
            return CommonResult.failed("获取互动游戏失败：" + e.getMessage());
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
