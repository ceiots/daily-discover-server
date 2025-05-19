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
import com.example.model.Shop;
import com.example.model.Specification;
import com.example.service.ProductService;
import com.example.service.ShopService;
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

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/random")
    public List<Product> getRandomProducts() {
        return productService.getRandomProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
    
    /**
     * 根据用户ID获取商品列表
     */
    @GetMapping("/user")
    public CommonResult<List<Product>> getUserProducts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<Product> products = productService.getProductsByUserId(userId);
            return CommonResult.success(products);
        } catch (Exception e) {
            log.error("获取用户商品列表时发生异常", e);
            return CommonResult.failed("获取用户商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据店铺ID获取商品列表
     */
    @GetMapping("/shop/{shopId}")
    public CommonResult<List<Product>> getProductsByShopId(@PathVariable Long shopId) {
        try {
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
            
            // 设置商品关联信息
            product.setUserId(userId);
            product.setShopId(shop.getId());
            // 设置审核状态为待审核
            product.setAuditStatus(0);
            
            // 如果有多个图片，使用第一张作为主图
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                product.setImageUrl(product.getImages().get(0));
            }
            
            // 对规格参数进行处理
            if (product.getSpecifications() != null) {
                for (int i = 0; i < product.getSpecifications().size(); i++) {
                    Specification spec = product.getSpecifications().get(i);
                    if (spec.getValues() == null) {
                        spec.setValues(new ArrayList<>());
                    }
                }
            }
            
            // 如果details字段映射成功但productDetails为空，则将details赋值给productDetails
            if (product.getDetails() != null && (product.getProductDetails() == null || product.getProductDetails().isEmpty())) {
                product.setProductDetails(product.getDetails());
            }
            // 备用处理：如果JsonProperty映射失败，从requestBody中获取details
            else if (product.getProductDetails() == null && requestBody.containsKey("details") && requestBody.get("details") instanceof List) {
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
                product.setProductDetails(productDetails);
            }
            
            // 确保stock字段有值
            if (requestBody.containsKey("stock")) {
                Object stockObj = requestBody.get("stock");
                if (stockObj instanceof Number) {
                    Integer stock = ((Number) stockObj).intValue();
                    product.setStock(stock);
                } else if (stockObj instanceof String) {
                    try {
                        Integer stock = Integer.parseInt((String)stockObj);
                        product.setStock(stock);
                    } catch (NumberFormatException e) {
                        product.setStock(0); // 默认值
                    }
                } else {
                    product.setStock(0); // 默认值
                }
            } else {
                product.setStock(0); // 默认值
            }
            
            Product createdProduct = productService.createProduct(product);
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
            
            if (!existingProduct.getUserId().equals(userId)) {
                return CommonResult.failed("您没有权限修改该商品");
            }
            
            existingProduct.setTitle(title);
            existingProduct.setPrice(new java.math.BigDecimal(price));
            existingProduct.setCategoryId(categoryId);
            
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
            
            if (!existingProduct.getUserId().equals(userId)) {
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
}