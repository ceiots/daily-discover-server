package com.dailydiscover.service;

import com.dailydiscover.model.InventoryItem;
import com.dailydiscover.model.InventoryBatch;
import com.dailydiscover.model.InventoryMovement;
import com.dailydiscover.model.InventoryAlert;
import com.dailydiscover.model.InventoryReport;
import com.dailydiscover.model.InventoryAdjustment;
import com.dailydiscover.model.InventoryTransfer;
import java.util.List;
import java.util.Map;

/**
 * 库存管理服务接口
 * 处理库存基础管理、库存预警、库存统计、批次管理等功能
 */
public interface InventoryService {
    
    // ==================== 库存基础管理 ====================
    
    /**
     * 根据ID获取库存项
     */
    InventoryItem findItemById(Long id);
    
    /**
     * 根据SKU获取库存项
     */
    InventoryItem findItemBySku(String sku);
    
    /**
     * 根据产品ID获取库存项
     */
    InventoryItem findItemByProductId(Long productId);
    
    /**
     * 保存库存项
     */
    void saveItem(InventoryItem item);
    
    /**
     * 更新库存项
     */
    void updateItem(InventoryItem item);
    
    /**
     * 删除库存项
     */
    void deleteItem(Long id);
    
    /**
     * 获取所有库存项
     */
    List<InventoryItem> findAllItems();
    
    /**
     * 根据条件查询库存项
     */
    List<InventoryItem> findItemsByCriteria(Map<String, Object> criteria);
    
    /**
     * 分页查询库存项
     */
    List<InventoryItem> findItemsPaginated(int page, int size);
    
    /**
     * 获取库存项总数
     */
    Long countAllItems();
    
    // ==================== 库存操作 ====================
    
    /**
     * 入库操作
     */
    void stockIn(Long itemId, Integer quantity, String batchNumber, String reason);
    
    /**
     * 出库操作
     */
    void stockOut(Long itemId, Integer quantity, String reason);
    
    /**
     * 库存调拨
     */
    void transferStock(InventoryTransfer transfer);
    
    /**
     * 库存调整
     */
    void adjustStock(InventoryAdjustment adjustment);
    
    /**
     * 库存盘点
     */
    void conductInventoryCheck(Long itemId, Integer actualQuantity, String notes);
    
    /**
     * 批量入库
     */
    void batchStockIn(List<Map<String, Object>> stockInList);
    
    /**
     * 批量出库
     */
    void batchStockOut(List<Map<String, Object>> stockOutList);
    
    /**
     * 获取库存流水记录
     */
    List<InventoryMovement> findMovementsByItemId(Long itemId);
    
    /**
     * 获取库存调整记录
     */
    List<InventoryAdjustment> findAdjustmentsByItemId(Long itemId);
    
    // ==================== 库存预警和补货 ====================
    
    /**
     * 检查库存预警
     */
    List<InventoryAlert> checkInventoryAlerts();
    
    /**
     * 获取库存预警列表
     */
    List<InventoryAlert> findAllAlerts();
    
    /**
     * 创建库存预警
     */
    void createAlert(InventoryAlert alert);
    
    /**
     * 更新库存预警
     */
    void updateAlert(InventoryAlert alert);
    
    /**
     * 删除库存预警
     */
    void deleteAlert(Long id);
    
    /**
     * 处理库存预警
     */
    void handleAlert(Long alertId, String action);
    
    /**
     * 自动补货建议
     */
    List<Map<String, Object>> getReplenishmentSuggestions();
    
    /**
     * 执行补货操作
     */
    void executeReplenishment(Long itemId, Integer quantity);
    
    /**
     * 获取安全库存设置
     */
    Map<String, Object> getSafetyStockSettings(Long itemId);
    
    /**
     * 更新安全库存设置
     */
    void updateSafetyStockSettings(Long itemId, Integer minStock, Integer maxStock);
    
    // ==================== 库存统计和分析 ====================
    
    /**
     * 获取库存统计报告
     */
    InventoryReport getInventoryReport(String period);
    
    /**
     * 获取库存周转率分析
     */
    Map<String, Object> getInventoryTurnoverAnalysis(String period);
    
    /**
     * 获取库存成本分析
     */
    Map<String, Object> getInventoryCostAnalysis(String period);
    
    /**
     * 获取库存效率分析
     */
    Map<String, Object> getInventoryEfficiencyAnalysis(String period);
    
    /**
     * 获取库存价值分析
     */
    Map<String, Object> getInventoryValueAnalysis(String period);
    
    /**
     * 获取库存ABC分析
     */
    Map<String, Object> getInventoryABCAnalysis();
    
    /**
     * 获取库存预测
     */
    Map<String, Object> getInventoryForecast(Long itemId, Integer days);
    
    // ==================== 批次管理 ====================
    
    /**
     * 创建库存批次
     */
    void createBatch(InventoryBatch batch);
    
    /**
     * 根据批次号获取批次信息
     */
    InventoryBatch findBatchByNumber(String batchNumber);
    
    /**
     * 获取库存项的所有批次
     */
    List<InventoryBatch> findBatchesByItemId(Long itemId);
    
    /**
     * 更新批次信息
     */
    void updateBatch(InventoryBatch batch);
    
    /**
     * 删除批次
     */
    void deleteBatch(Long id);
    
    /**
     * 批次库存查询
     */
    List<InventoryBatch> findBatchesByCriteria(Map<String, Object> criteria);
    
    /**
     * 获取即将过期的批次
     */
    List<InventoryBatch> findExpiringBatches(Integer days);
    
    /**
     * 处理过期批次
     */
    void handleExpiredBatch(Long batchId, String action);
    
    /**
     * 批次质量检查
     */
    void conductBatchQualityCheck(Long batchId, String result, String notes);
    
    // ==================== 保质期管理 ====================
    
    /**
     * 检查保质期预警
     */
    List<InventoryBatch> checkExpiryAlerts(Integer days);
    
    /**
     * 获取保质期统计
     */
    Map<String, Object> getExpiryStatistics();
    
    /**
     * 处理临期商品
     */
    void handleExpiringProducts(List<Long> batchIds, String strategy);
    
    /**
     * 获取保质期分析报告
     */
    Map<String, Object> getExpiryAnalysisReport(String period);
    
    // ==================== 库存优化 ====================
    
    /**
     * 优化库存水平
     */
    Map<String, Object> optimizeInventoryLevels();
    
    /**
     * 优化库存布局
     */
    Map<String, Object> optimizeInventoryLayout();
    
    /**
     * 优化补货策略
     */
    Map<String, Object> optimizeReplenishmentStrategy();
    
    /**
     * 库存成本优化
     */
    Map<String, Object> optimizeInventoryCosts();
    
    /**
     * 库存周转优化
     */
    Map<String, Object> optimizeInventoryTurnover();
    
    // ==================== 多仓库管理 ====================
    
    /**
     * 获取仓库列表
     */
    List<Map<String, Object>> getWarehouseList();
    
    /**
     * 获取仓库库存
     */
    List<InventoryItem> getWarehouseInventory(Long warehouseId);
    
    /**
     * 仓库间调拨
     */
    void transferBetweenWarehouses(Long fromWarehouseId, Long toWarehouseId, Long itemId, Integer quantity);
    
    /**
     * 获取仓库利用率
     */
    Map<String, Object> getWarehouseUtilization(Long warehouseId);
    
    /**
     * 优化仓库布局
     */
    Map<String, Object> optimizeWarehouseLayout(Long warehouseId);
    
    // ==================== 库存盘点 ====================
    
    /**
     * 创建盘点任务
     */
    void createInventoryCheckTask(Long warehouseId, String checkType);
    
    /**
     * 执行盘点
     */
    void executeInventoryCheck(Long taskId, Map<Long, Integer> actualQuantities);
    
    /**
     * 获取盘点结果
     */
    Map<String, Object> getInventoryCheckResult(Long taskId);
    
    /**
     * 处理盘点差异
     */
    void handleInventoryVariance(Long taskId, Map<Long, String> varianceActions);
    
    /**
     * 获取盘点历史
     */
    List<Map<String, Object>> getInventoryCheckHistory(String period);
    
    // ==================== 库存成本控制 ====================
    
    /**
     * 计算库存持有成本
     */
    Double calculateHoldingCost(Long itemId);
    
    /**
     * 计算库存缺货成本
     */
    Double calculateStockoutCost(Long itemId);
    
    /**
     * 计算库存订购成本
     */
    Double calculateOrderingCost(Long itemId);
    
    /**
     * 优化库存成本结构
     */
    Map<String, Object> optimizeCostStructure();
    
    /**
     * 获取成本分析报告
     */
    Map<String, Object> getCostAnalysisReport(String period);
    
    // ==================== 批量操作 ====================
    
    /**
     * 批量更新库存
     */
    void batchUpdateInventory(List<Map<String, Object>> updates);
    
    /**
     * 批量创建库存项
     */
    void batchCreateItems(List<InventoryItem> items);
    
    /**
     * 批量删除库存项
     */
    void batchDeleteItems(List<Long> itemIds);
    
    /**
     * 批量导入库存数据
     */
    void batchImportInventoryData(List<Map<String, Object>> data);
    
    /**
     * 批量导出库存报告
     */
    List<Map<String, Object>> batchExportInventoryReport(List<Long> itemIds);
    
    /**
     * 批量处理库存预警
     */
    void batchHandleAlerts(List<Long> alertIds, String action);
    
    // ==================== 高级分析功能 ====================
    
    /**
     * 库存需求预测
     */
    Map<String, Object> forecastInventoryDemand(Long itemId, Integer period);
    
    /**
     * 库存风险分析
     */
    Map<String, Object> analyzeInventoryRisks();
    
    /**
     * 库存绩效评估
     */
    Map<String, Object> evaluateInventoryPerformance(String period);
    
    /**
     * 库存策略优化
     */
    Map<String, Object> optimizeInventoryStrategy();
    
    /**
     * 供应链库存协同
     */
    Map<String, Object> coordinateSupplyChainInventory();
}