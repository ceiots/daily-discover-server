package com.example.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Calendar;
import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.model.ProductDetail;
import com.example.model.ProductSku;
import com.example.model.ProductContent;
import com.example.model.Shop;
import com.example.model.Specification;
import com.example.service.ProductService;
import com.example.service.ProductSkuService;
import com.example.service.ShopService;
import com.example.service.RecommendationService;
import com.example.util.UserIdExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private ProductSkuService productSkuService;

    @GetMapping("")
    public CommonResult<Map<String, Object>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            List<Product> products = productService.getProductsWithPagination(page, size);
            int total = productService.countApprovedProducts();
            int totalPages = (int) Math.ceil((double) total / size);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", products);
            result.put("totalElements", total);
            result.put("totalPages", totalPages);
            result.put("number", page);
            result.put("size", size);
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取商品列表时发生异常", e);
            return CommonResult.failed("获取商品列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Product> getAllProductsWithoutPaging() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public CommonResult<Product> getProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            if (id == null) {
                return CommonResult.failed("商品ID不能为空");
            }
            
            // 获取当前用户ID，可能为null
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            
            // 根据权限获取商品信息
            Product product = productService.getProductByIdWithPermission(id, userId);
            
            if (product == null) {
                return CommonResult.failed("商品不存在或无权查看");
            }
            
            // 获取商品的SKU列表
            List<ProductSku> skus = productSkuService.getSkusByProductId(id);
            product.setSkus(skus);
            
            // 如果商品未通过审核，加入提示信息
            if (product.getAuditStatus() != null && product.getAuditStatus() != 1) {
                log.info("访问未通过审核的商品, id: {}, status: {}, userId: {}", 
                        id, product.getAuditStatus(), userId);
                // 此处可以添加产品状态的特殊标记
                product.setAuditRemark("该商品正在审核中，仅您可见");
            }
            
            return CommonResult.success(product);
        } catch (Exception e) {
            log.error("获取商品详情时发生异常, id: {}", id, e);
            return CommonResult.failed("获取商品详情失败，请稍后重试");
        }
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/random")
    public List<Product> getRandomProducts(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return productService.getRandomProducts(limit);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
    
    /**
     * 根据店铺ID获取商品列表
     */
    @GetMapping("/shop")
    public CommonResult<List<Product>> getProductsByShopId(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam(value = "shopId", required = true) Long shopId) {
        try {
            if (shopId == null) {
                return CommonResult.failed("店铺ID不能为空");
            }
            
            List<Product> products = productService.getProductsByShopId(shopId);
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取店铺商品列表时发生异常", e);
            return CommonResult.failed("获取店铺商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据店铺ID获取商品列表
     */
    @GetMapping("/shop/{shopId}")
    public CommonResult<List<Product>> getShopProducts(@PathVariable Long shopId) {
        try {
            if (shopId == null) {
                return CommonResult.failed("店铺ID不能为空");
            }
            
            List<Product> products = productService.getProductsByShopId(shopId);
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取店铺商品列表时发生异常", e);
            return CommonResult.failed("获取店铺商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传商品图片
     * @param files 图片文件，可以是单个文件或多个文件
     * @param token JWT令牌
     * @return 通用结果，包含图片URL列表
     */
    @PostMapping("/upload")
    public CommonResult<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile[] files,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 检查用户是否有店铺
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null) {
                return CommonResult.failed("您还没有创建店铺，请先创建店铺");
            }
            
            if (files.length == 0) {
                return CommonResult.failed("上传的文件为空");
            }
            
            // 存储所有上传成功的图片URL
            List<String> successUrls = new ArrayList<>();
            List<Map<String, String>> errorFiles = new ArrayList<>();
            
            // 处理每个文件
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                
                try {
                    // 生成唯一的文件名
                    String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                    
                    // 尝试本地保存文件
                    String imageUrl = saveFileLocally(file, fileName);
                    if (imageUrl != null) {
                        // 本地保存成功
                        successUrls.add(imageUrl);
                    } else {
                        // 保存失败，记录错误
                        Map<String, String> errorFile = new HashMap<>();
                        errorFile.put("name", file.getOriginalFilename());
                        errorFile.put("error", "文件保存失败");
                        errorFiles.add(errorFile);
                    }
                } catch (Exception e) {
                    // 处理单个文件上传失败
                    log.error("上传单个文件失败: {}", file.getOriginalFilename(), e);
                    Map<String, String> errorFile = new HashMap<>();
                    errorFile.put("name", file.getOriginalFilename());
                    errorFile.put("error", e.getMessage());
                    errorFiles.add(errorFile);
                }
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            
            if (successUrls.isEmpty() && !errorFiles.isEmpty()) {
                // 所有文件都上传失败
                return CommonResult.failed("所有文件上传失败");
            } else if (!successUrls.isEmpty() && !errorFiles.isEmpty()) {
                // 部分文件上传成功
                result.put("urls", successUrls);
                result.put("errorFiles", errorFiles);
                result.put("partialSuccess", true);
                // 返回第一个成功上传的URL作为主URL
                result.put("url", successUrls.get(0));
                return CommonResult.success(result);
            } else {
                // 所有文件都上传成功
                result.put("urls", successUrls);
                // 返回第一个上传的URL作为主URL
                result.put("url", successUrls.get(0));
                return CommonResult.success(result);
            }
        } catch (Exception e) {
            log.error("上传图片时发生异常", e);
            return CommonResult.failed("上传图片失败：" + e.getMessage());
        }
    }
    
    /**
     * 本地保存文件
     * @param file 要保存的文件
     * @param fileName 文件名
     * @return 访问URL，保存失败则返回null
     */
    private String saveFileLocally(MultipartFile file, String fileName) {
        try {
            // 获取当前日期，用于创建文件夹层级
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要+1
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            // 构建目录结构：基础目录/年/月/日/
            String baseDir = "E:/media/product/";
            String dateDir = year + "/" + (month < 10 ? "0" + month : month) + "/" + (day < 10 ? "0" + day : day) + "/";
            String uploadDir = baseDir + dateDir;
            
            // 检查上传目录是否存在，不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                // 创建目录结构
                if (!dir.mkdirs()) {
                    log.error("无法创建按日期分层的上传目录: {}", uploadDir);
                    
                    // 如果创建日期目录失败，回退到基础目录
                    log.info("尝试使用基础目录保存文件");
                    dir = new File(baseDir);
                    if (!dir.exists() && !dir.mkdirs()) {
                        log.error("无法创建基础上传目录: {}", baseDir);
                        return null;
                    }
                    dateDir = ""; // 重置日期目录路径
                }
            }
            
            // 保存文件
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);
            
            log.info("商品图片已成功保存到本地: {}", destFile.getAbsolutePath());
            
            // 返回URL
            return "https://dailydiscover.top/media/product/" + dateDir + fileName;
        } catch (IOException e) {
            log.error("本地保存文件失败", e);
            return null;
        }
    }
    
    /**
     * 创建商品
     */
    @PostMapping("/create")
    public CommonResult<Product> createProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("createProduct: " + requestBody);
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 检查用户是否有店铺
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null) {
                return CommonResult.failed("您还没有创建店铺，请先创建店铺");
            }
            
            // 检查店铺状态
            if (shop.getStatus() != 1) {
                return CommonResult.failed("您的店铺当前状态不允许创建商品");
            }
            
            // 将requestBody转换为Product对象
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.convertValue(requestBody, Product.class);
            System.out.println("product: " + product);
            
            // 处理SKU数据
            List<ProductSku> skus = null;
            if (requestBody.containsKey("skus") && requestBody.get("skus") instanceof List) {
                skus = objectMapper.convertValue(requestBody.get("skus"), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ProductSku.class));
            }
            
            product.setShopId(shop.getId());
            // 设置审核状态为待审核
            product.setAuditStatus(0);
            
            // 如果有多个图片，使用第一张作为主图
            if (product.getContent() != null && product.getContent().getImages() != null && !product.getContent().getImages().isEmpty()) {
                product.setImageUrl(product.getContent().getImages().get(0));
            }
            
            // 设置总库存，如果未设置则默认为0
            if (product.getTotalStock() == null) {
                product.setTotalStock(0);
            }
            
            // 如果details字段映射成功但productDetails为空，则将details赋值给productDetails
            if (product.getContent() != null && product.getContent().getDetails() != null 
                && product.getContent().getDetails().isEmpty()) {
                // No need to set details to itself
                log.info("Product has empty details list");
            }
            // 备用处理：如果JsonProperty映射失败，从requestBody中获取details
            else if ((product.getContent() == null || product.getContent().getDetails() == null) 
                    && requestBody.containsKey("details") && requestBody.get("details") instanceof List) {
                List<Map<String, Object>> details = (List<Map<String, Object>>) requestBody.get("details");
                System.out.println("details: " + details);
                List<ProductDetail> productDetails = new ArrayList<>();
                for (Map<String, Object> detail : details) {
                    ProductDetail pd = new ProductDetail();
                    pd.setType((String) detail.get("type"));
                    pd.setContent((String) detail.get("content"));
                    
                    // 处理sort，确保是Integer类型
                    Object sortObj = detail.get("sort");
                    if (sortObj != null) {
                        if (sortObj instanceof Integer) {
                            pd.setSort((Integer) sortObj);
                        } else if (sortObj instanceof Number) {
                            pd.setSort(((Number) sortObj).intValue());
                        } else {
                            pd.setSort(0); // 默认值
                        }
                    } else {
                        pd.setSort(0); // 默认值
                    }
                    
                    productDetails.add(pd);
                }
                
                // 初始化Product的content字段（如果为null）
                if (product.getContent() == null) {
                    product.setContent(new ProductContent());
                }
                product.getContent().setDetails(productDetails);
                
                // 处理图片列表
                if (requestBody.containsKey("images") && requestBody.get("images") instanceof List) {
                    List<String> images = (List<String>) requestBody.get("images");
                    product.getContent().setImages(images);
                    
                    // 设置主图
                    if (!images.isEmpty() && (product.getImageUrl() == null || product.getImageUrl().isEmpty())) {
                        product.setImageUrl(images.get(0));
                    }
                }
            }
            
            // 创建商品
            Product createdProduct = productService.createProduct(product);
            
            // 如果有SKU数据，创建SKU
            if (skus != null && !skus.isEmpty()) {
                // 为每个SKU设置productId
                for (ProductSku sku : skus) {
                    sku.setProductId(createdProduct.getId());
                }
                
                // 批量创建SKU
                List<ProductSku> createdSkus = productSkuService.batchCreateSku(skus);
                
                // 计算总库存
                int totalStock = 0;
                for (ProductSku sku : createdSkus) {
                    totalStock += sku.getStock();
                }
                
                // 更新商品总库存
                createdProduct.setTotalStock(totalStock);
                productService.updateProduct(createdProduct);
                
                // 设置SKU列表到返回的商品对象
                createdProduct.setSkus(createdSkus);
            }
            
            return CommonResult.success(createdProduct);
        } catch (Exception e) {
            log.error("创建商品时发生异常", e);
            return CommonResult.failed("创建商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public CommonResult<Product> updateProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("title") String title,
            @RequestParam("price") String price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "totalStock", required = false) Integer totalStock,
            @RequestParam(value = "productSkuId", required = false) Long productSkuId,
            @RequestParam(value = "specifications", required = false) String specifications,
            @RequestParam(value = "productDetails", required = false) String productDetails,
            @RequestParam(value = "purchaseNotices", required = false) String purchaseNotices,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            // 检查用户是否是店铺的拥有者
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null || !shop.getId().equals(existingProduct.getShopId())) {
                return CommonResult.failed("您没有权限修改该商品");
            }
            
            existingProduct.setTitle(title);
            existingProduct.setPrice(new java.math.BigDecimal(price));
            existingProduct.setCategoryId(categoryId);
            
            // 更新总库存和SKU ID
            if (totalStock != null) {
                existingProduct.setTotalStock(totalStock);
            }
            
            if (productSkuId != null) {
                if (existingProduct.getSkus() != null && !existingProduct.getSkus().isEmpty()) {
                    existingProduct.getSkus().get(0).setId(productSkuId);
                } else {
                    // 如果没有SKU，创建一个默认SKU
                    ProductSku defaultSku = new ProductSku();
                    defaultSku.setId(productSkuId);
                    defaultSku.setProductId(id);
                    defaultSku.setPrice(existingProduct.getPrice());
                    defaultSku.setStock(existingProduct.getTotalStock() != null ? existingProduct.getTotalStock() : 0);
                    List<ProductSku> skus = new ArrayList<>();
                    skus.add(defaultSku);
                    existingProduct.setSkus(skus);
                }
            }
            
            Product updatedProduct = productService.updateProduct(existingProduct);
            return CommonResult.success(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品时发生异常", e);
            return CommonResult.failed("更新商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            // 检查用户是否是店铺的拥有者
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null || !shop.getId().equals(existingProduct.getShopId())) {
                return CommonResult.failed("您没有权限删除该商品");
            }
            
            productService.deleteProduct(id);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("删除商品时发生异常", e);
            return CommonResult.failed("删除商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品审核接口 (管理员使用)
     */
    @PostMapping("/{id}/audit")
    public CommonResult<Product> auditProduct(
            @PathVariable Long id,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String auditRemark,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // TODO: 应当进行管理员权限检查
            
            Product product = productService.auditProduct(id, auditStatus, auditRemark);
            if (product == null) {
                return CommonResult.failed("商品不存在");
            }
            
            return CommonResult.success(product);
        } catch (Exception e) {
            log.error("审核商品时发生异常", e);
            return CommonResult.failed("审核商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取待审核商品列表 (管理员使用)
     */
    @GetMapping("/pending-audit")
    public CommonResult<List<Product>> getPendingAuditProducts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // TODO: 应当进行管理员权限检查
            
            List<Product> products = productService.getPendingAuditProducts();
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取待审核商品列表时发生异常", e);
            return CommonResult.failed("获取待审核商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取个性化商品推荐，包含匹配分数和AI洞察
     */
    @GetMapping("/recommendations")
    public CommonResult<Map<String, Object>> getPersonalizedRecommendations(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            // 获取用户ID，未登录用户也可以获取推荐，但不会是个性化的
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            
            // 获取推荐商品列表及相关数据
            Map<String, Object> recommendationsData = recommendationService.getPersonalizedRecommendations(userId);
            
            return CommonResult.success(recommendationsData);
        } catch (Exception e) {
            log.error("获取个性化商品推荐时发生异常", e);
            return CommonResult.failed("获取推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品的SKU列表
     */
    @GetMapping("/{productId}/skus")
    public CommonResult<List<ProductSku>> getProductSkus(@PathVariable Long productId) {
        try {
            List<ProductSku> skus = productSkuService.getSkusByProductId(productId);
            return CommonResult.success(skus);
        } catch (Exception e) {
            log.error("获取商品SKU列表时发生异常", e);
            return CommonResult.failed("获取商品SKU列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建商品SKU
     */
    @PostMapping("/{productId}/skus")
    public CommonResult<List<ProductSku>> createProductSkus(
            @PathVariable Long productId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody List<ProductSku> skus) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(productId);
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            // 检查用户是否是店铺的拥有者
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null || !shop.getId().equals(existingProduct.getShopId())) {
                return CommonResult.failed("您没有权限为该商品添加SKU");
            }
            
            // 设置productId
            for (ProductSku sku : skus) {
                sku.setProductId(productId);
            }
            
            // 创建SKU
            List<ProductSku> createdSkus = productSkuService.batchCreateSku(skus);
            
            // 更新商品的总库存
            int totalStock = 0;
            for (ProductSku sku : createdSkus) {
                totalStock += sku.getStock();
            }
            existingProduct.setTotalStock(totalStock);
            productService.updateProduct(existingProduct);
            
            return CommonResult.success(createdSkus);
        } catch (Exception e) {
            log.error("创建商品SKU时发生异常", e);
            return CommonResult.failed("创建商品SKU失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品SKU
     */
    @PutMapping("/skus/{skuId}")
    public CommonResult<ProductSku> updateProductSku(
            @PathVariable Long skuId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody ProductSku sku) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 获取SKU信息
            ProductSku existingSku = productSkuService.getSkuById(skuId);
            if (existingSku == null) {
                return CommonResult.failed("SKU不存在");
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(existingSku.getProductId());
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            // 检查用户是否是店铺的拥有者
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null || !shop.getId().equals(existingProduct.getShopId())) {
                return CommonResult.failed("您没有权限修改该SKU");
            }
            
            // 设置ID确保更新正确的记录
            sku.setId(skuId);
            sku.setProductId(existingSku.getProductId());
            
            // 更新SKU
            ProductSku updatedSku = productSkuService.updateSku(sku);
            
            // 更新商品的总库存
            List<ProductSku> allSkus = productSkuService.getSkusByProductId(existingSku.getProductId());
            int totalStock = 0;
            for (ProductSku s : allSkus) {
                totalStock += s.getStock();
            }
            existingProduct.setTotalStock(totalStock);
            productService.updateProduct(existingProduct);
            
            return CommonResult.success(updatedSku);
        } catch (Exception e) {
            log.error("更新商品SKU时发生异常", e);
            return CommonResult.failed("更新商品SKU失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品SKU
     */
    @DeleteMapping("/skus/{skuId}")
    public CommonResult<Void> deleteProductSku(
            @PathVariable Long skuId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 获取SKU信息
            ProductSku existingSku = productSkuService.getSkuById(skuId);
            if (existingSku == null) {
                return CommonResult.failed("SKU不存在");
            }
            
            // 验证商品所有权
            Product existingProduct = productService.getProductById(existingSku.getProductId());
            if (existingProduct == null) {
                return CommonResult.failed("商品不存在");
            }
            
            // 检查用户是否是店铺的拥有者
            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null || !shop.getId().equals(existingProduct.getShopId())) {
                return CommonResult.failed("您没有权限删除该SKU");
            }
            
            // 删除SKU
            productSkuService.deleteSku(skuId);
            
            // 更新商品的总库存
            List<ProductSku> remainingSkus = productSkuService.getSkusByProductId(existingSku.getProductId());
            int totalStock = 0;
            for (ProductSku s : remainingSkus) {
                totalStock += s.getStock();
            }
            existingProduct.setTotalStock(totalStock);
            productService.updateProduct(existingProduct);
            
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("删除商品SKU时发生异常", e);
            return CommonResult.failed("删除商品SKU失败：" + e.getMessage());
        }
    }
}