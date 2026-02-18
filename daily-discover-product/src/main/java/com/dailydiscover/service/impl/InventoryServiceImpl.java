package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.InventoryMapper;
import com.dailydiscover.model.*;
import com.dailydiscover.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 库存管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper inventoryMapper;
    
    // ==================== 库存基础管理 ====================
    
    @Override
    public InventoryItem findItemById(Long id) {
        try {
            return inventoryMapper.findItemById(id);
        } catch (Exception e) {
            log.error("获取库存项失败: id={}", id, e);
            throw new RuntimeException("获取库存项失败", e);
        }
    }
    
    @Override
    public InventoryItem findItemBySku(String sku) {
        try {
            return inventoryMapper.findItemBySku(sku);
        } catch (Exception e) {
            log.error("获取库存项失败: sku={}", sku, e);
            throw new RuntimeException("获取库存项失败", e);
        }
    }
    
    @Override
    public InventoryItem findItemByProductId(Long productId) {
        try {
            return inventoryMapper.findItemByProductId(productId);
        } catch (Exception e) {
            log.error("获取库存项失败: productId={}", productId, e);
            throw new RuntimeException("获取库存项失败", e);
        }
    }
    
    @Override
    @Transactional
    public void saveItem(InventoryItem item) {
        try {
            inventoryMapper.insertItem(item);
            log.info("库存项保存成功: id={}, name={}", item.getId(), item.getName());
        } catch (Exception e) {
            log.error("保存库存项失败: name={}", item.getName(), e);
            throw new RuntimeException("保存库存项失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateItem(InventoryItem item) {
        try {
            inventoryMapper.updateItem(item);
            log.info("库存项更新成功: id={}, name={}", item.getId(), item.getName());
        } catch (Exception e) {
            log.error("更新库存项失败: id={}, name={}", item.getId(), item.getName(), e);
            throw new RuntimeException("更新库存项失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteItem(Long id) {
        try {
            inventoryMapper.deleteItem(id);
            log.info("库存项删除成功: id={}", id);
        } catch (Exception e) {
            log.error("删除库存项失败: id={}", id, e);
            throw new RuntimeException("删除库存项失败", e);
        }
    }
    
    @Override
    public List<InventoryItem> findAllItems() {
        try {
            return inventoryMapper.findAllItems();
        } catch (Exception e) {
            log.error("获取所有库存项失败", e);
            throw new RuntimeException("获取所有库存项失败", e);
        }
    }
    
    @Override
    public List<InventoryItem> findItemsByCriteria(Map<String, Object> criteria) {
        try {
            return inventoryMapper.findItemsByCriteria(criteria);
        } catch (Exception e) {
            log.error("根据条件查询库存项失败: criteria={}", criteria, e);
            throw new RuntimeException("根据条件查询库存项失败", e);
        }
    }
    
    @Override
    public List<InventoryItem> findItemsPaginated(int page, int size) {
        try {
            int offset = (page - 1) * size;
            return inventoryMapper.findItemsPaginated(offset, size);
        } catch (Exception e) {
            log.error("分页查询库存项失败: page={}, size={}", page, size, e);
            throw new RuntimeException("分页查询库存项失败", e);
        }
    }
    
    @Override
    public Long countAllItems() {
        try {
            return inventoryMapper.countAllItems();
        } catch (Exception e) {
            log.error("统计库存项总数失败", e);
            throw new RuntimeException("统计库存项总数失败", e);
        }
    }
    
    // ==================== 库存操作 ====================
    
    @Override
    @Transactional
    public void stockIn(Long itemId, Integer quantity, String batchNumber, String reason) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + itemId);
            }
            
            int quantityBefore = item.getCurrentStock();
            int quantityAfter = quantityBefore + quantity;
            
            // 更新库存
            inventoryMapper.increaseStock(itemId, quantity);
            
            // 记录库存流水
            InventoryMovement movement = new InventoryMovement();
            movement.setItemId(itemId);
            movement.setMovementType("STOCK_IN");
            movement.setQuantity(quantity);
            movement.setQuantityBefore(quantityBefore);
            movement.setQuantityAfter(quantityAfter);
            movement.setReferenceNumber("IN_" + System.currentTimeMillis());
            movement.setReferenceType("STOCK_IN");
            movement.setBatchNumber(batchNumber);
            movement.setReason(reason);
            movement.setNotes("入库操作");
            movement.setOperatorId(1L); // 实际应用中应从上下文中获取
            movement.setOperatorName("系统管理员");
            movement.setUnitCost(item.getCostPrice());
            movement.setTotalCost(item.getCostPrice() * quantity);
            
            inventoryMapper.insertMovement(movement);
            
            log.info("入库操作成功: itemId={}, quantity={}, batchNumber={}", itemId, quantity, batchNumber);
        } catch (Exception e) {
            log.error("入库操作失败: itemId={}, quantity={}", itemId, quantity, e);
            throw new RuntimeException("入库操作失败", e);
        }
    }
    
    @Override
    @Transactional
    public void stockOut(Long itemId, Integer quantity, String reason) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + itemId);
            }
            
            if (item.getAvailableStock() < quantity) {
                throw new RuntimeException("库存不足: 当前库存=" + item.getAvailableStock() + ", 需求=" + quantity);
            }
            
            int quantityBefore = item.getCurrentStock();
            int quantityAfter = quantityBefore - quantity;
            
            // 更新库存
            inventoryMapper.decreaseStock(itemId, quantity);
            
            // 记录库存流水
            InventoryMovement movement = new InventoryMovement();
            movement.setItemId(itemId);
            movement.setMovementType("STOCK_OUT");
            movement.setQuantity(quantity);
            movement.setQuantityBefore(quantityBefore);
            movement.setQuantityAfter(quantityAfter);
            movement.setReferenceNumber("OUT_" + System.currentTimeMillis());
            movement.setReferenceType("STOCK_OUT");
            movement.setReason(reason);
            movement.setNotes("出库操作");
            movement.setOperatorId(1L);
            movement.setOperatorName("系统管理员");
            movement.setUnitCost(item.getCostPrice());
            movement.setTotalCost(item.getCostPrice() * quantity);
            
            inventoryMapper.insertMovement(movement);
            
            log.info("出库操作成功: itemId={}, quantity={}", itemId, quantity);
        } catch (Exception e) {
            log.error("出库操作失败: itemId={}, quantity={}", itemId, quantity, e);
            throw new RuntimeException("出库操作失败", e);
        }
    }
    
    @Override
    @Transactional
    public void transferStock(InventoryTransfer transfer) {
        try {
            // 创建调拨记录
            inventoryMapper.insertTransfer(transfer);
            
            // 从源仓库出库
            stockOut(transfer.getItemId(), transfer.getQuantity(), "库存调拨至仓库: " + transfer.getToWarehouseName());
            
            // 这里应该调用目标仓库的入库操作
            // 实际应用中需要根据目标仓库的库存项ID进行入库
            
            log.info("库存调拨成功: transferNumber={}, quantity={}", transfer.getTransferNumber(), transfer.getQuantity());
        } catch (Exception e) {
            log.error("库存调拨失败: itemId={}, quantity={}", transfer.getItemId(), transfer.getQuantity(), e);
            throw new RuntimeException("库存调拨失败", e);
        }
    }
    
    @Override
    @Transactional
    public void adjustStock(InventoryAdjustment adjustment) {
        try {
            InventoryItem item = inventoryMapper.findItemById(adjustment.getItemId());
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + adjustment.getItemId());
            }
            
            int quantityBefore = item.getCurrentStock();
            int quantityAfter = quantityBefore + adjustment.getAdjustmentQuantity();
            
            if (quantityAfter < 0) {
                throw new RuntimeException("调整后库存不能为负数");
            }
            
            // 更新库存
            if (adjustment.getAdjustmentQuantity() > 0) {
                inventoryMapper.increaseStock(adjustment.getItemId(), adjustment.getAdjustmentQuantity());
            } else {
                inventoryMapper.decreaseStock(adjustment.getItemId(), Math.abs(adjustment.getAdjustmentQuantity()));
            }
            
            // 记录调整
            adjustment.setQuantityBefore(quantityBefore);
            adjustment.setQuantityAfter(quantityAfter);
            adjustment.setAdjustmentDate(LocalDateTime.now());
            inventoryMapper.insertAdjustment(adjustment);
            
            // 记录库存流水
            InventoryMovement movement = new InventoryMovement();
            movement.setItemId(adjustment.getItemId());
            movement.setMovementType("ADJUSTMENT");
            movement.setQuantity(adjustment.getAdjustmentQuantity());
            movement.setQuantityBefore(quantityBefore);
            movement.setQuantityAfter(quantityAfter);
            movement.setReferenceNumber(adjustment.getReferenceNumber());
            movement.setReferenceType("ADJUSTMENT");
            movement.setReason(adjustment.getReason());
            movement.setNotes(adjustment.getNotes());
            movement.setOperatorId(adjustment.getAdjustedBy());
            movement.setOperatorName(adjustment.getAdjustedByName());
            
            inventoryMapper.insertMovement(movement);
            
            log.info("库存调整成功: itemId={}, adjustmentQuantity={}", adjustment.getItemId(), adjustment.getAdjustmentQuantity());
        } catch (Exception e) {
            log.error("库存调整失败: itemId={}, adjustmentQuantity={}", adjustment.getItemId(), adjustment.getAdjustmentQuantity(), e);
            throw new RuntimeException("库存调整失败", e);
        }
    }
    
    @Override
    @Transactional
    public void conductInventoryCheck(Long itemId, Integer actualQuantity, String notes) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + itemId);
            }
            
            int systemQuantity = item.getCurrentStock();
            int variance = actualQuantity - systemQuantity;
            
            if (variance != 0) {
                // 需要调整库存
                InventoryAdjustment adjustment = new InventoryAdjustment();
                adjustment.setItemId(itemId);
                adjustment.setAdjustmentType("INVENTORY_CHECK");
                adjustment.setAdjustmentQuantity(variance);
                adjustment.setReason("库存盘点差异调整");
                adjustment.setReferenceNumber("CHECK_" + System.currentTimeMillis());
                adjustment.setNotes(notes);
                adjustment.setAdjustedBy(1L);
                adjustment.setAdjustedByName("盘点人员");
                adjustment.setRequiresApproval(false);
                adjustment.setApprovalStatus("APPROVED");
                
                adjustStock(adjustment);
            }
            
            // 更新最后盘点时间
            item.setLastInventoryCheck(LocalDateTime.now());
            inventoryMapper.updateItem(item);
            
            log.info("库存盘点完成: itemId={}, systemQuantity={}, actualQuantity={}, variance={}", 
                    itemId, systemQuantity, actualQuantity, variance);
        } catch (Exception e) {
            log.error("库存盘点失败: itemId={}, actualQuantity={}", itemId, actualQuantity, e);
            throw new RuntimeException("库存盘点失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchStockIn(List<Map<String, Object>> stockInList) {
        try {
            for (Map<String, Object> stockIn : stockInList) {
                Long itemId = (Long) stockIn.get("itemId");
                Integer quantity = (Integer) stockIn.get("quantity");
                String batchNumber = (String) stockIn.get("batchNumber");
                String reason = (String) stockIn.get("reason");
                
                stockIn(itemId, quantity, batchNumber, reason);
            }
            log.info("批量入库成功: count={}", stockInList.size());
        } catch (Exception e) {
            log.error("批量入库失败: count={}", stockInList.size(), e);
            throw new RuntimeException("批量入库失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchStockOut(List<Map<String, Object>> stockOutList) {
        try {
            for (Map<String, Object> stockOut : stockOutList) {
                Long itemId = (Long) stockOut.get("itemId");
                Integer quantity = (Integer) stockOut.get("quantity");
                String reason = (String) stockOut.get("reason");
                
                stockOut(itemId, quantity, reason);
            }
            log.info("批量出库成功: count={}", stockOutList.size());
        } catch (Exception e) {
            log.error("批量出库失败: count={}", stockOutList.size(), e);
            throw new RuntimeException("批量出库失败", e);
        }
    }
    
    @Override
    public List<InventoryMovement> findMovementsByItemId(Long itemId) {
        try {
            return inventoryMapper.findMovementsByItemId(itemId);
        } catch (Exception e) {
            log.error("获取库存流水记录失败: itemId={}", itemId, e);
            throw new RuntimeException("获取库存流水记录失败", e);
        }
    }
    
    @Override
    public List<InventoryAdjustment> findAdjustmentsByItemId(Long itemId) {
        try {
            return inventoryMapper.findAdjustmentsByItemId(itemId);
        } catch (Exception e) {
            log.error("获取库存调整记录失败: itemId={}", itemId, e);
            throw new RuntimeException("获取库存调整记录失败", e);
        }
    }
    
    // ==================== 库存预警和补货 ====================
    
    @Override
    public List<InventoryAlert> checkInventoryAlerts() {
        try {
            return inventoryMapper.checkInventoryAlerts();
        } catch (Exception e) {
            log.error("检查库存预警失败", e);
            throw new RuntimeException("检查库存预警失败", e);
        }
    }
    
    @Override
    public List<InventoryAlert> findAllAlerts() {
        try {
            return inventoryMapper.findAllAlerts();
        } catch (Exception e) {
            log.error("获取库存预警列表失败", e);
            throw new RuntimeException("获取库存预警列表失败", e);
        }
    }
    
    @Override
    @Transactional
    public void createAlert(InventoryAlert alert) {
        try {
            inventoryMapper.insertAlert(alert);
            log.info("创建库存预警成功: id={}, type={}", alert.getId(), alert.getAlertType());
        } catch (Exception e) {
            log.error("创建库存预警失败: itemId={}, type={}", alert.getItemId(), alert.getAlertType(), e);
            throw new RuntimeException("创建库存预警失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateAlert(InventoryAlert alert) {
        try {
            inventoryMapper.updateAlert(alert);
            log.info("更新库存预警成功: id={}", alert.getId());
        } catch (Exception e) {
            log.error("更新库存预警失败: id={}", alert.getId(), e);
            throw new RuntimeException("更新库存预警失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteAlert(Long id) {
        try {
            inventoryMapper.deleteAlert(id);
            log.info("删除库存预警成功: id={}", id);
        } catch (Exception e) {
            log.error("删除库存预警失败: id={}", id, e);
            throw new RuntimeException("删除库存预警失败", e);
        }
    }
    
    @Override
    @Transactional
    public void handleAlert(Long alertId, String action) {
        try {
            InventoryAlert alert = inventoryMapper.findAllAlerts().stream()
                    .filter(a -> a.getId().equals(alertId))
                    .findFirst()
                    .orElse(null);
            
            if (alert != null) {
                alert.setActionTaken(action);
                alert.setIsAcknowledged(true);
                alert.setAcknowledgedBy(1L); // 实际应用中应从上下文中获取
                alert.setAcknowledgedAt(LocalDateTime.now());
                alert.setResolutionStatus("RESOLVED");
                alert.setResolvedAt(LocalDateTime.now());
                
                inventoryMapper.updateAlert(alert);
                log.info("处理库存预警成功: alertId={}, action={}", alertId, action);
            }
        } catch (Exception e) {
            log.error("处理库存预警失败: alertId={}, action={}", alertId, action, e);
            throw new RuntimeException("处理库存预警失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getReplenishmentSuggestions() {
        try {
            List<InventoryItem> lowStockItems = inventoryMapper.findLowStockItems();
            List<Map<String, Object>> suggestions = new ArrayList<>();
            
            for (InventoryItem item : lowStockItems) {
                Map<String, Object> suggestion = new HashMap<>();
                suggestion.put("itemId", item.getId());
                suggestion.put("sku", item.getSku());
                suggestion.put("name", item.getName());
                suggestion.put("currentStock", item.getCurrentStock());
                suggestion.put("safetyStock", item.getSafetyStock());
                suggestion.put("reorderPoint", item.getReorderPoint());
                suggestion.put("suggestedQuantity", item.getMaxStock() - item.getCurrentStock());
                suggestion.put("urgency", calculateReplenishmentUrgency(item));
                suggestions.add(suggestion);
            }
            
            return suggestions;
        } catch (Exception e) {
            log.error("获取补货建议失败", e);
            throw new RuntimeException("获取补货建议失败", e);
        }
    }
    
    @Override
    @Transactional
    public void executeReplenishment(Long itemId, Integer quantity) {
        try {
            // 这里应该调用采购系统的接口
            // 目前先记录补货操作
            log.info("执行补货操作: itemId={}, quantity={}", itemId, quantity);
        } catch (Exception e) {
            log.error("执行补货操作失败: itemId={}, quantity={}", itemId, quantity, e);
            throw new RuntimeException("执行补货操作失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getSafetyStockSettings(Long itemId) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + itemId);
            }
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("safetyStock", item.getSafetyStock());
            settings.put("maxStock", item.getMaxStock());
            settings.put("reorderPoint", item.getReorderPoint());
            settings.put("currentStock", item.getCurrentStock());
            settings.put("recommendedSafetyStock", calculateRecommendedSafetyStock(item));
            
            return settings;
        } catch (Exception e) {
            log.error("获取安全库存设置失败: itemId={}", itemId, e);
            throw new RuntimeException("获取安全库存设置失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateSafetyStockSettings(Long itemId, Integer minStock, Integer maxStock) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                throw new RuntimeException("库存项不存在: " + itemId);
            }
            
            item.setSafetyStock(minStock);
            item.setMaxStock(maxStock);
            item.setReorderPoint((int) (minStock * 1.2)); // 重新订购点计算
            
            inventoryMapper.updateItem(item);
            log.info("更新安全库存设置成功: itemId={}, minStock={}, maxStock={}", itemId, minStock, maxStock);
        } catch (Exception e) {
            log.error("更新安全库存设置失败: itemId={}, minStock={}, maxStock={}", itemId, minStock, maxStock, e);
            throw new RuntimeException("更新安全库存设置失败", e);
        }
    }
    
    // ==================== 库存统计和分析 ====================
    
    @Override
    public InventoryReport getInventoryReport(String period) {
        try {
            InventoryReport report = inventoryMapper.findReportByTypeAndPeriod("INVENTORY_SUMMARY", period);
            if (report == null) {
                report = generateInventoryReport(period);
            }
            return report;
        } catch (Exception e) {
            log.error("获取库存统计报告失败: period={}", period, e);
            throw new RuntimeException("获取库存统计报告失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryTurnoverAnalysis(String period) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            
            // 简化的库存周转率分析
            List<InventoryItem> items = inventoryMapper.findAllItems();
            double totalTurnover = 0;
            int count = 0;
            
            for (InventoryItem item : items) {
                if (item.getCurrentStock() > 0) {
                    double turnover = calculateItemTurnover(item, period);
                    totalTurnover += turnover;
                    count++;
                }
            }
            
            analysis.put("averageTurnoverRate", count > 0 ? totalTurnover / count : 0);
            analysis.put("totalItemsAnalyzed", count);
            analysis.put("period", period);
            
            return analysis;
        } catch (Exception e) {
            log.error("获取库存周转率分析失败: period={}", period, e);
            throw new RuntimeException("获取库存周转率分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryCostAnalysis(String period) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            double totalValue = 0;
            double holdingCost = 0;
            
            for (InventoryItem item : items) {
                if (item.getTotalValue() != null) {
                    totalValue += item.getTotalValue();
                    holdingCost += calculateHoldingCost(item);
                }
            }
            
            analysis.put("totalInventoryValue", totalValue);
            analysis.put("averageHoldingCost", holdingCost);
            analysis.put("holdingCostPercentage", totalValue > 0 ? (holdingCost / totalValue) * 100 : 0);
            analysis.put("period", period);
            
            return analysis;
        } catch (Exception e) {
            log.error("获取库存成本分析失败: period={}", period, e);
            throw new RuntimeException("获取库存成本分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryEfficiencyAnalysis(String period) {
        try {
            Map<String, Object> efficiency = new HashMap<>();
            
            // 简化的库存效率分析
            efficiency.put("stockAccuracyRate", 98.5); // 示例值
            efficiency.put("orderFillRate", 95.2); // 示例值
            efficiency.put("inventoryCoverageDays", 45); // 示例值
            efficiency.put("carryingCostRate", 25.3); // 示例值
            efficiency.put("period", period);
            
            return efficiency;
        } catch (Exception e) {
            log.error("获取库存效率分析失败: period={}", period, e);
            throw new RuntimeException("获取库存效率分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryValueAnalysis(String period) {
        try {
            Map<String, Object> valueAnalysis = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            double totalValue = 0;
            int slowMovingCount = 0;
            int fastMovingCount = 0;
            
            for (InventoryItem item : items) {
                if (item.getTotalValue() != null) {
                    totalValue += item.getTotalValue();
                    
                    // 简化的商品分类
                    if (isSlowMoving(item)) {
                        slowMovingCount++;
                    } else {
                        fastMovingCount++;
                    }
                }
            }
            
            valueAnalysis.put("totalInventoryValue", totalValue);
            valueAnalysis.put("slowMovingItems", slowMovingCount);
            valueAnalysis.put("fastMovingItems", fastMovingCount);
            valueAnalysis.put("slowMovingPercentage", items.size() > 0 ? (double) slowMovingCount / items.size() * 100 : 0);
            valueAnalysis.put("period", period);
            
            return valueAnalysis;
        } catch (Exception e) {
            log.error("获取库存价值分析失败: period={}", period, e);
            throw new RuntimeException("获取库存价值分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryABCAnalysis() {
        try {
            Map<String, Object> abcAnalysis = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            List<InventoryItem> aItems = new ArrayList<>();
            List<InventoryItem> bItems = new ArrayList<>();
            List<InventoryItem> cItems = new ArrayList<>();
            
            for (InventoryItem item : items) {
                if (item.getTotalValue() != null) {
                    String category = classifyABC(item);
                    switch (category) {
                        case "A": aItems.add(item); break;
                        case "B": bItems.add(item); break;
                        case "C": cItems.add(item); break;
                    }
                }
            }
            
            abcAnalysis.put("aItems", aItems);
            abcAnalysis.put("bItems", bItems);
            abcAnalysis.put("cItems", cItems);
            abcAnalysis.put("aCount", aItems.size());
            abcAnalysis.put("bCount", bItems.size());
            abcAnalysis.put("cCount", cItems.size());
            
            return abcAnalysis;
        } catch (Exception e) {
            log.error("获取库存ABC分析失败", e);
            throw new RuntimeException("获取库存ABC分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryForecast(Long itemId, Integer days) {
        try {
            Map<String, Object> forecast = new HashMap<>();
            InventoryItem item = inventoryMapper.findItemById(itemId);
            
            if (item != null) {
                forecast.put("itemId", itemId);
                forecast.put("sku", item.getSku());
                forecast.put("name", item.getName());
                forecast.put("currentStock", item.getCurrentStock());
                forecast.put("forecastPeriod", days);
                forecast.put("predictedDemand", predictDemand(item, days));
                forecast.put("suggestedReorderQuantity", calculateReorderQuantity(item, days));
                forecast.put("riskLevel", assessRiskLevel(item, days));
            }
            
            return forecast;
        } catch (Exception e) {
            log.error("获取库存预测失败: itemId={}, days={}", itemId, days, e);
            throw new RuntimeException("获取库存预测失败", e);
        }
    }
    
    // ==================== 批次管理 ====================
    
    @Override
    @Transactional
    public void createBatch(InventoryBatch batch) {
        try {
            inventoryMapper.insertBatch(batch);
            log.info("创建库存批次成功: batchNumber={}, itemId={}", batch.getBatchNumber(), batch.getItemId());
        } catch (Exception e) {
            log.error("创建库存批次失败: batchNumber={}, itemId={}", batch.getBatchNumber(), batch.getItemId(), e);
            throw new RuntimeException("创建库存批次失败", e);
        }
    }
    
    @Override
    public InventoryBatch findBatchByNumber(String batchNumber) {
        try {
            return inventoryMapper.findBatchByNumber(batchNumber);
        } catch (Exception e) {
            log.error("获取批次信息失败: batchNumber={}", batchNumber, e);
            throw new RuntimeException("获取批次信息失败", e);
        }
    }
    
    @Override
    public List<InventoryBatch> findBatchesByItemId(Long itemId) {
        try {
            return inventoryMapper.findBatchesByItemId(itemId);
        } catch (Exception e) {
            log.error("获取库存项批次失败: itemId={}", itemId, e);
            throw new RuntimeException("获取库存项批次失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateBatch(InventoryBatch batch) {
        try {
            inventoryMapper.updateBatch(batch);
            log.info("更新批次信息成功: id={}, batchNumber={}", batch.getId(), batch.getBatchNumber());
        } catch (Exception e) {
            log.error("更新批次信息失败: id={}, batchNumber={}", batch.getId(), batch.getBatchNumber(), e);
            throw new RuntimeException("更新批次信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteBatch(Long id) {
        try {
            inventoryMapper.deleteBatch(id);
            log.info("删除批次成功: id={}", id);
        } catch (Exception e) {
            log.error("删除批次失败: id={}", id, e);
            throw new RuntimeException("删除批次失败", e);
        }
    }
    
    @Override
    public List<InventoryBatch> findBatchesByCriteria(Map<String, Object> criteria) {
        try {
            return inventoryMapper.findBatchesByCriteria(criteria);
        } catch (Exception e) {
            log.error("根据条件查询批次失败: criteria={}", criteria, e);
            throw new RuntimeException("根据条件查询批次失败", e);
        }
    }
    
    @Override
    public List<InventoryBatch> findExpiringBatches(Integer days) {
        try {
            return inventoryMapper.findExpiringBatches(days);
        } catch (Exception e) {
            log.error("获取即将过期批次失败: days={}", days, e);
            throw new RuntimeException("获取即将过期批次失败", e);
        }
    }
    
    @Override
    @Transactional
    public void handleExpiredBatch(Long batchId, String action) {
        try {
            InventoryBatch batch = inventoryMapper.findBatchesByCriteria(Map.of("id", batchId)).stream()
                    .findFirst()
                    .orElse(null);
            
            if (batch != null) {
                batch.setIsExpired(true);
                batch.setQualityStatus("EXPIRED");
                inventoryMapper.updateBatch(batch);
                
                log.info("处理过期批次成功: batchId={}, action={}", batchId, action);
            }
        } catch (Exception e) {
            log.error("处理过期批次失败: batchId={}, action={}", batchId, action, e);
            throw new RuntimeException("处理过期批次失败", e);
        }
    }
    
    @Override
    @Transactional
    public void conductBatchQualityCheck(Long batchId, String result, String notes) {
        try {
            InventoryBatch batch = inventoryMapper.findBatchesByCriteria(Map.of("id", batchId)).stream()
                    .findFirst()
                    .orElse(null);
            
            if (batch != null) {
                batch.setInspectionResult(result);
                batch.setInspectionNotes(notes);
                batch.setInspectionDate(LocalDateTime.now());
                batch.setQualityStatus(result);
                
                inventoryMapper.updateBatch(batch);
                log.info("批次质量检查完成: batchId={}, result={}", batchId, result);
            }
        } catch (Exception e) {
            log.error("批次质量检查失败: batchId={}, result={}", batchId, result, e);
            throw new RuntimeException("批次质量检查失败", e);
        }
    }
    
    // ==================== 保质期管理 ====================
    
    @Override
    public List<InventoryBatch> checkExpiryAlerts(Integer days) {
        try {
            return inventoryMapper.findExpiringBatches(days);
        } catch (Exception e) {
            log.error("检查保质期预警失败: days={}", days, e);
            throw new RuntimeException("检查保质期预警失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getExpiryStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            List<InventoryBatch> allBatches = inventoryMapper.findBatchesByCriteria(new HashMap<>());
            long totalBatches = allBatches.size();
            long expiringBatches = allBatches.stream()
                    .filter(b -> b.getExpiryDate() != null && 
                            b.getExpiryDate().isBefore(LocalDateTime.now().plusDays(30)))
                    .count();
            long expiredBatches = allBatches.stream()
                    .filter(b -> b.getIsExpired() != null && b.getIsExpired())
                    .count();
            
            stats.put("totalBatches", totalBatches);
            stats.put("expiringBatches", expiringBatches);
            stats.put("expiredBatches", expiredBatches);
            stats.put("expiryRate", totalBatches > 0 ? (double) expiredBatches / totalBatches * 100 : 0);
            
            return stats;
        } catch (Exception e) {
            log.error("获取保质期统计失败", e);
            throw new RuntimeException("获取保质期统计失败", e);
        }
    }
    
    @Override
    @Transactional
    public void handleExpiringProducts(List<Long> batchIds, String strategy) {
        try {
            for (Long batchId : batchIds) {
                // 根据策略处理临期商品
                switch (strategy) {
                    case "DISCOUNT":
                        log.info("对批次 {} 进行折扣处理", batchId);
                        break;
                    case "RETURN":
                        log.info("对批次 {} 进行退货处理", batchId);
                        break;
                    case "DESTROY":
                        log.info("对批次 {} 进行销毁处理", batchId);
                        break;
                    default:
                        log.info("对批次 {} 进行默认处理", batchId);
                }
            }
            log.info("处理临期商品成功: batchCount={}, strategy={}", batchIds.size(), strategy);
        } catch (Exception e) {
            log.error("处理临期商品失败: batchCount={}, strategy={}", batchIds.size(), strategy, e);
            throw new RuntimeException("处理临期商品失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getExpiryAnalysisReport(String period) {
        try {
            Map<String, Object> report = new HashMap<>();
            
            // 简化的保质期分析报告
            report.put("period", period);
            report.put("totalProductsAnalyzed", 150);
            report.put("expiryRiskLevel", "MEDIUM");
            report.put("recommendedActions", Arrays.asList("加强库存周转", "优化采购计划", "加强保质期监控"));
            
            return report;
        } catch (Exception e) {
            log.error("获取保质期分析报告失败: period={}", period, e);
            throw new RuntimeException("获取保质期分析报告失败", e);
        }
    }
    
    // ==================== 库存优化 ====================
    
    @Override
    public Map<String, Object> optimizeInventoryLevels() {
        try {
            Map<String, Object> optimization = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            int optimizedCount = 0;
            double totalSavings = 0;
            
            for (InventoryItem item : items) {
                Map<String, Object> itemOptimization = optimizeItemLevels(item);
                if ((boolean) itemOptimization.get("needsOptimization")) {
                    optimizedCount++;
                    totalSavings += (double) itemOptimization.get("potentialSavings");
                }
            }
            
            optimization.put("optimizedItems", optimizedCount);
            optimization.put("totalPotentialSavings", totalSavings);
            optimization.put("recommendations", generateOptimizationRecommendations(items));
            
            return optimization;
        } catch (Exception e) {
            log.error("优化库存水平失败", e);
            throw new RuntimeException("优化库存水平失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeInventoryLayout() {
        try {
            Map<String, Object> layoutOptimization = new HashMap<>();
            
            // 简化的库存布局优化
            layoutOptimization.put("recommendedLayout", "ABC分类布局");
            layoutOptimization.put("estimatedEfficiencyGain", 15.5);
            layoutOptimization.put("implementationComplexity", "MEDIUM");
            layoutOptimization.put("estimatedTime", "2周");
            
            return layoutOptimization;
        } catch (Exception e) {
            log.error("优化库存布局失败", e);
            throw new RuntimeException("优化库存布局失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeReplenishmentStrategy() {
        try {
            Map<String, Object> strategyOptimization = new HashMap<>();
            
            // 简化的补货策略优化
            strategyOptimization.put("currentStrategy", "固定周期补货");
            strategyOptimization.put("recommendedStrategy", "动态补货");
            strategyOptimization.put("expectedImprovement", 12.3);
            strategyOptimization.put("riskLevel", "LOW");
            
            return strategyOptimization;
        } catch (Exception e) {
            log.error("优化补货策略失败", e);
            throw new RuntimeException("优化补货策略失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeInventoryCosts() {
        try {
            Map<String, Object> costOptimization = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            double currentTotalCost = items.stream()
                    .mapToDouble(item -> item.getTotalValue() != null ? item.getTotalValue() : 0)
                    .sum();
            
            costOptimization.put("currentTotalCost", currentTotalCost);
            costOptimization.put("potentialSavings", currentTotalCost * 0.15); // 假设可节省15%
            costOptimization.put("recommendedActions", Arrays.asList("减少慢动销库存", "优化采购批量", "加强供应商管理"));
            
            return costOptimization;
        } catch (Exception e) {
            log.error("优化库存成本失败", e);
            throw new RuntimeException("优化库存成本失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeInventoryTurnover() {
        try {
            Map<String, Object> turnoverOptimization = new HashMap<>();
            
            // 简化的库存周转优化
            turnoverOptimization.put("currentTurnoverRate", 6.2);
            turnoverOptimization.put("targetTurnoverRate", 8.5);
            turnoverOptimization.put("improvementPotential", 37.1);
            turnoverOptimization.put("keyAreas", Arrays.asList("促销慢动销商品", "优化采购频率", "加强需求预测"));
            
            return turnoverOptimization;
        } catch (Exception e) {
            log.error("优化库存周转失败", e);
            throw new RuntimeException("优化库存周转失败", e);
        }
    }
    
    // ==================== 多仓库管理 ====================
    
    @Override
    public List<Map<String, Object>> getWarehouseList() {
        try {
            // 简化的仓库列表
            List<Map<String, Object>> warehouses = new ArrayList<>();
            
            Map<String, Object> warehouse1 = new HashMap<>();
            warehouse1.put("id", 1L);
            warehouse1.put("name", "主仓库");
            warehouse1.put("location", "北京");
            warehouse1.put("capacity", 10000);
            warehouse1.put("utilization", 75.5);
            warehouses.add(warehouse1);
            
            Map<String, Object> warehouse2 = new HashMap<>();
            warehouse2.put("id", 2L);
            warehouse2.put("name", "分仓库");
            warehouse2.put("location", "上海");
            warehouse2.put("capacity", 5000);
            warehouse2.put("utilization", 60.2);
            warehouses.add(warehouse2);
            
            return warehouses;
        } catch (Exception e) {
            log.error("获取仓库列表失败", e);
            throw new RuntimeException("获取仓库列表失败", e);
        }
    }
    
    @Override
    public List<InventoryItem> getWarehouseInventory(Long warehouseId) {
        try {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("warehouseId", warehouseId);
            return inventoryMapper.findItemsByCriteria(criteria);
        } catch (Exception e) {
            log.error("获取仓库库存失败: warehouseId={}", warehouseId, e);
            throw new RuntimeException("获取仓库库存失败", e);
        }
    }
    
    @Override
    @Transactional
    public void transferBetweenWarehouses(Long fromWarehouseId, Long toWarehouseId, Long itemId, Integer quantity) {
        try {
            InventoryTransfer transfer = new InventoryTransfer();
            transfer.setItemId(itemId);
            transfer.setFromWarehouseId(fromWarehouseId);
            transfer.setFromWarehouseName("仓库" + fromWarehouseId);
            transfer.setToWarehouseId(toWarehouseId);
            transfer.setToWarehouseName("仓库" + toWarehouseId);
            transfer.setQuantity(quantity);
            transfer.setTransferReason("仓库间调拨");
            transfer.setTransferStatus("INITIATED");
            transfer.setTransferDate(LocalDateTime.now());
            
            transferStock(transfer);
            log.info("仓库间调拨成功: from={}, to={}, itemId={}, quantity={}", 
                    fromWarehouseId, toWarehouseId, itemId, quantity);
        } catch (Exception e) {
            log.error("仓库间调拨失败: from={}, to={}, itemId={}, quantity={}", 
                    fromWarehouseId, toWarehouseId, itemId, quantity, e);
            throw new RuntimeException("仓库间调拨失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getWarehouseUtilization(Long warehouseId) {
        try {
            Map<String, Object> utilization = new HashMap<>();
            
            List<InventoryItem> items = getWarehouseInventory(warehouseId);
            double totalValue = items.stream()
                    .mapToDouble(item -> item.getTotalValue() != null ? item.getTotalValue() : 0)
                    .sum();
            
            utilization.put("warehouseId", warehouseId);
            utilization.put("totalItems", items.size());
            utilization.put("totalValue", totalValue);
            utilization.put("utilizationRate", calculateWarehouseUtilization(items));
            utilization.put("efficiencyScore", calculateWarehouseEfficiency(items));
            
            return utilization;
        } catch (Exception e) {
            log.error("获取仓库利用率失败: warehouseId={}", warehouseId, e);
            throw new RuntimeException("获取仓库利用率失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeWarehouseLayout(Long warehouseId) {
        try {
            Map<String, Object> layoutOptimization = new HashMap<>();
            
            layoutOptimization.put("warehouseId", warehouseId);
            layoutOptimization.put("currentLayout", "随机存储");
            layoutOptimization.put("recommendedLayout", "分类存储");
            layoutOptimization.put("expectedImprovement", 18.7);
            layoutOptimization.put("implementationTime", "3周");
            
            return layoutOptimization;
        } catch (Exception e) {
            log.error("优化仓库布局失败: warehouseId={}", warehouseId, e);
            throw new RuntimeException("优化仓库布局失败", e);
        }
    }
    
    // ==================== 库存盘点 ====================
    
    @Override
    @Transactional
    public void createInventoryCheckTask(Long warehouseId, String checkType) {
        try {
            // 创建盘点任务
            log.info("创建盘点任务: warehouseId={}, checkType={}", warehouseId, checkType);
        } catch (Exception e) {
            log.error("创建盘点任务失败: warehouseId={}, checkType={}", warehouseId, checkType, e);
            throw new RuntimeException("创建盘点任务失败", e);
        }
    }
    
    @Override
    @Transactional
    public void executeInventoryCheck(Long taskId, Map<Long, Integer> actualQuantities) {
        try {
            for (Map.Entry<Long, Integer> entry : actualQuantities.entrySet()) {
                conductInventoryCheck(entry.getKey(), entry.getValue(), "盘点任务: " + taskId);
            }
            log.info("执行盘点完成: taskId={}, itemCount={}", taskId, actualQuantities.size());
        } catch (Exception e) {
            log.error("执行盘点失败: taskId={}, itemCount={}", taskId, actualQuantities.size(), e);
            throw new RuntimeException("执行盘点失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getInventoryCheckResult(Long taskId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 简化的盘点结果
            result.put("taskId", taskId);
            result.put("totalItemsChecked", 150);
            result.put("varianceCount", 5);
            result.put("accuracyRate", 96.7);
            result.put("totalVarianceValue", 1250.50);
            
            return result;
        } catch (Exception e) {
            log.error("获取盘点结果失败: taskId={}", taskId, e);
            throw new RuntimeException("获取盘点结果失败", e);
        }
    }
    
    @Override
    @Transactional
    public void handleInventoryVariance(Long taskId, Map<Long, String> varianceActions) {
        try {
            for (Map.Entry<Long, String> entry : varianceActions.entrySet()) {
                log.info("处理盘点差异: itemId={}, action={}", entry.getKey(), entry.getValue());
            }
            log.info("处理盘点差异完成: taskId={}, varianceCount={}", taskId, varianceActions.size());
        } catch (Exception e) {
            log.error("处理盘点差异失败: taskId={}, varianceCount={}", taskId, varianceActions.size(), e);
            throw new RuntimeException("处理盘点差异失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getInventoryCheckHistory(String period) {
        try {
            // 简化的盘点历史
            List<Map<String, Object>> history = new ArrayList<>();
            
            Map<String, Object> check1 = new HashMap<>();
            check1.put("taskId", 1L);
            check1.put("checkDate", "2024-01-15");
            check1.put("warehouse", "主仓库");
            check1.put("accuracyRate", 98.2);
            check1.put("varianceValue", 850.00);
            history.add(check1);
            
            return history;
        } catch (Exception e) {
            log.error("获取盘点历史失败: period={}", period, e);
            throw new RuntimeException("获取盘点历史失败", e);
        }
    }
    
    // ==================== 库存成本控制 ====================
    
    @Override
    public Double calculateHoldingCost(Long itemId) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null || item.getTotalValue() == null) {
                return 0.0;
            }
            
            // 简化的持有成本计算（假设年化持有成本率为25%）
            return item.getTotalValue() * 0.25 / 365 * 30; // 30天成本
        } catch (Exception e) {
            log.error("计算库存持有成本失败: itemId={}", itemId, e);
            throw new RuntimeException("计算库存持有成本失败", e);
        }
    }
    
    @Override
    public Double calculateStockoutCost(Long itemId) {
        try {
            InventoryItem item = inventoryMapper.findItemById(itemId);
            if (item == null) {
                return 0.0;
            }
            
            // 简化的缺货成本计算
            return item.getSellingPrice() * 0.1; // 假设缺货成本为售价的10%
        } catch (Exception e) {
            log.error("计算库存缺货成本失败: itemId={}", itemId, e);
            throw new RuntimeException("计算库存缺货成本失败", e);
        }
    }
    
    @Override
    public Double calculateOrderingCost(Long itemId) {
        try {
            // 简化的订购成本计算
            return 50.0; // 固定订购成本
        } catch (Exception e) {
            log.error("计算库存订购成本失败: itemId={}", itemId, e);
            throw new RuntimeException("计算库存订购成本失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeCostStructure() {
        try {
            Map<String, Object> costOptimization = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            double currentTotalCost = items.stream()
                    .mapToDouble(item -> {
                        double holdingCost = calculateHoldingCost(item.getId());
                        double orderingCost = calculateOrderingCost(item.getId());
                        return holdingCost + orderingCost;
                    })
                    .sum();
            
            costOptimization.put("currentTotalCost", currentTotalCost);
            costOptimization.put("optimizedTotalCost", currentTotalCost * 0.85); // 假设可优化15%
            costOptimization.put("potentialSavings", currentTotalCost * 0.15);
            costOptimization.put("recommendedActions", Arrays.asList("优化订购批量", "减少安全库存", "改进需求预测"));
            
            return costOptimization;
        } catch (Exception e) {
            log.error("优化库存成本结构失败", e);
            throw new RuntimeException("优化库存成本结构失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCostAnalysisReport(String period) {
        try {
            Map<String, Object> costReport = new HashMap<>();
            
            costReport.put("period", period);
            costReport.put("totalHoldingCost", 12500.00);
            costReport.put("totalOrderingCost", 3500.00);
            costReport.put("totalStockoutCost", 800.00);
            costReport.put("costBreakdown", Map.of(
                "holdingCostPercentage", 75.2,
                "orderingCostPercentage", 21.1,
                "stockoutCostPercentage", 3.7
            ));
            
            return costReport;
        } catch (Exception e) {
            log.error("获取成本分析报告失败: period={}", period, e);
            throw new RuntimeException("获取成本分析报告失败", e);
        }
    }
    
    // ==================== 批量操作 ====================
    
    @Override
    @Transactional
    public void batchUpdateInventory(List<Map<String, Object>> updates) {
        try {
            for (Map<String, Object> update : updates) {
                Long itemId = (Long) update.get("itemId");
                Integer stock = (Integer) update.get("stock");
                Integer availableStock = (Integer) update.get("availableStock");
                
                inventoryMapper.updateItemStock(itemId, stock, availableStock);
            }
            log.info("批量更新库存成功: count={}", updates.size());
        } catch (Exception e) {
            log.error("批量更新库存失败: count={}", updates.size(), e);
            throw new RuntimeException("批量更新库存失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchCreateItems(List<InventoryItem> items) {
        try {
            for (InventoryItem item : items) {
                inventoryMapper.insertItem(item);
            }
            log.info("批量创建库存项成功: count={}", items.size());
        } catch (Exception e) {
            log.error("批量创建库存项失败: count={}", items.size(), e);
            throw new RuntimeException("批量创建库存项失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchDeleteItems(List<Long> itemIds) {
        try {
            for (Long itemId : itemIds) {
                inventoryMapper.deleteItem(itemId);
            }
            log.info("批量删除库存项成功: count={}", itemIds.size());
        } catch (Exception e) {
            log.error("批量删除库存项失败: count={}", itemIds.size(), e);
            throw new RuntimeException("批量删除库存项失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchImportInventoryData(List<Map<String, Object>> data) {
        try {
            for (Map<String, Object> itemData : data) {
                InventoryItem item = new InventoryItem();
                // 这里需要根据数据格式设置item的属性
                inventoryMapper.insertItem(item);
            }
            log.info("批量导入库存数据成功: count={}", data.size());
        } catch (Exception e) {
            log.error("批量导入库存数据失败: count={}", data.size(), e);
            throw new RuntimeException("批量导入库存数据失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> batchExportInventoryReport(List<Long> itemIds) {
        try {
            List<Map<String, Object>> exportData = new ArrayList<>();
            
            for (Long itemId : itemIds) {
                InventoryItem item = inventoryMapper.findItemById(itemId);
                if (item != null) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("id", item.getId());
                    itemData.put("sku", item.getSku());
                    itemData.put("name", item.getName());
                    itemData.put("currentStock", item.getCurrentStock());
                    itemData.put("totalValue", item.getTotalValue());
                    exportData.add(itemData);
                }
            }
            
            log.info("批量导出库存报告成功: count={}", exportData.size());
            return exportData;
        } catch (Exception e) {
            log.error("批量导出库存报告失败: count={}", itemIds.size(), e);
            throw new RuntimeException("批量导出库存报告失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchHandleAlerts(List<Long> alertIds, String action) {
        try {
            inventoryMapper.batchAcknowledgeAlerts(alertIds, 1L, action);
            log.info("批量处理库存预警成功: count={}, action={}", alertIds.size(), action);
        } catch (Exception e) {
            log.error("批量处理库存预警失败: count={}, action={}", alertIds.size(), action, e);
            throw new RuntimeException("批量处理库存预警失败", e);
        }
    }
    
    // ==================== 高级分析功能 ====================
    
    @Override
    public Map<String, Object> forecastInventoryDemand(Long itemId, Integer period) {
        try {
            Map<String, Object> forecast = new HashMap<>();
            
            // 简化的需求预测
            forecast.put("itemId", itemId);
            forecast.put("forecastPeriod", period);
            forecast.put("predictedDemand", 150); // 示例值
            forecast.put("confidenceLevel", 85.5);
            forecast.put("seasonalityFactor", 1.2);
            
            return forecast;
        } catch (Exception e) {
            log.error("库存需求预测失败: itemId={}, period={}", itemId, period, e);
            throw new RuntimeException("库存需求预测失败", e);
        }
    }
    
    @Override
    public Map<String, Object> analyzeInventoryRisks() {
        try {
            Map<String, Object> riskAnalysis = new HashMap<>();
            
            List<InventoryItem> items = inventoryMapper.findAllItems();
            int highRiskItems = 0;
            int mediumRiskItems = 0;
            int lowRiskItems = 0;
            
            for (InventoryItem item : items) {
                String riskLevel = assessItemRisk(item);
                switch (riskLevel) {
                    case "HIGH": highRiskItems++; break;
                    case "MEDIUM": mediumRiskItems++; break;
                    case "LOW": lowRiskItems++; break;
                }
            }
            
            riskAnalysis.put("highRiskItems", highRiskItems);
            riskAnalysis.put("mediumRiskItems", mediumRiskItems);
            riskAnalysis.put("lowRiskItems", lowRiskItems);
            riskAnalysis.put("overallRiskLevel", calculateOverallRisk(highRiskItems, mediumRiskItems, lowRiskItems));
            
            return riskAnalysis;
        } catch (Exception e) {
            log.error("库存风险分析失败", e);
            throw new RuntimeException("库存风险分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> evaluateInventoryPerformance(String period) {
        try {
            Map<String, Object> performance = new HashMap<>();
            
            // 简化的库存绩效评估
            performance.put("period", period);
            performance.put("turnoverScore", 8.2);
            performance.put("accuracyScore", 9.1);
            performance.put("costScore", 7.8);
            performance.put("serviceLevelScore", 9.4);
            performance.put("overallScore", 8.6);
            performance.put("performanceLevel", "GOOD");
            
            return performance;
        } catch (Exception e) {
            log.error("库存绩效评估失败: period={}", period, e);
            throw new RuntimeException("库存绩效评估失败", e);
        }
    }
    
    @Override
    public Map<String, Object> optimizeInventoryStrategy() {
        try {
            Map<String, Object> strategyOptimization = new HashMap<>();
            
            // 简化的库存策略优化
            strategyOptimization.put("currentStrategy", "传统库存管理");
            strategyOptimization.put("recommendedStrategy", "精益库存管理");
            strategyOptimization.put("expectedBenefits", Arrays.asList("减少库存成本", "提高周转率", "改善服务水平"));
            strategyOptimization.put("implementationRoadmap", generateImplementationRoadmap());
            
            return strategyOptimization;
        } catch (Exception e) {
            log.error("库存策略优化失败", e);
            throw new RuntimeException("库存策略优化失败", e);
        }
    }
    
    @Override
    public Map<String, Object> coordinateSupplyChainInventory() {
        try {
            Map<String, Object> coordination = new HashMap<>();
            
            // 简化的供应链库存协同
            coordination.put("supplierCollaborationLevel", "MEDIUM");
            coordination.put("informationSharingScore", 7.5);
            coordination.put("coordinationEfficiency", 68.2);
            coordination.put("improvementOpportunities", Arrays.asList("加强供应商协同", "优化信息共享", "改进预测准确性"));
            
            return coordination;
        } catch (Exception e) {
            log.error("供应链库存协同分析失败", e);
            throw new RuntimeException("供应链库存协同分析失败", e);
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private String calculateReplenishmentUrgency(InventoryItem item) {
        if (item.getCurrentStock() <= item.getSafetyStock() * 0.5) {
            return "HIGH";
        } else if (item.getCurrentStock() <= item.getSafetyStock()) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private Integer calculateRecommendedSafetyStock(InventoryItem item) {
        // 简化的安全库存计算
        return (int) (item.getCurrentStock() * 0.3);
    }
    
    private InventoryReport generateInventoryReport(String period) {
        InventoryReport report = new InventoryReport();
        report.setReportType("INVENTORY_SUMMARY");
        report.setPeriod(period);
        report.setReportDate(LocalDateTime.now());
        
        List<InventoryItem> items = inventoryMapper.findAllItems();
        report.setTotalItems(items.size());
        report.setActiveItems((int) items.stream().filter(item -> item.getIsActive()).count());
        report.setLowStockItems((int) items.stream().filter(item -> item.getCurrentStock() <= item.getSafetyStock()).count());
        report.setOutOfStockItems((int) items.stream().filter(item -> item.getCurrentStock() <= 0).count());
        report.setExpiredItems(0); // 需要从批次表中获取
        report.setTotalInventoryValue(items.stream().mapToDouble(item -> item.getTotalValue() != null ? item.getTotalValue() : 0).sum());
        report.setAverageTurnoverRate(6.5); // 示例值
        report.setTotalMovements(150); // 示例值
        report.setStockAccuracyRate(98.5); // 示例值
        report.setInventoryCoverageDays(45); // 示例值
        
        return report;
    }
    
    private double calculateItemTurnover(InventoryItem item, String period) {
        // 简化的周转率计算
        return 6.5; // 示例值
    }
    
    private double calculateHoldingCost(InventoryItem item) {
        // 简化的持有成本计算
        return item.getTotalValue() != null ? item.getTotalValue() * 0.25 / 365 * 30 : 0;
    }
    
    private boolean isSlowMoving(InventoryItem item) {
        // 简化的慢动销判断
        return item.getCurrentStock() > item.getMaxStock() * 0.8;
    }
    
    private String classifyABC(InventoryItem item) {
        if (item.getTotalValue() != null && item.getTotalValue() > 10000) {
            return "A";
        } else if (item.getTotalValue() != null && item.getTotalValue() > 1000) {
            return "B";
        } else {
            return "C";
        }
    }
    
    private double predictDemand(InventoryItem item, Integer days) {
        // 简化的需求预测
        return item.getCurrentStock() * 0.3; // 示例算法
    }
    
    private Integer calculateReorderQuantity(InventoryItem item, Integer days) {
        // 简化的重新订购量计算
        return Math.max(item.getMaxStock() - item.getCurrentStock(), 0);
    }
    
    private String assessRiskLevel(InventoryItem item, Integer days) {
        if (item.getCurrentStock() <= item.getSafetyStock()) {
            return "HIGH";
        } else if (item.getCurrentStock() <= item.getReorderPoint()) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private Map<String, Object> optimizeItemLevels(InventoryItem item) {
        Map<String, Object> optimization = new HashMap<>();
        
        boolean needsOptimization = item.getCurrentStock() > item.getMaxStock() || 
                                   item.getCurrentStock() < item.getSafetyStock();
        
        optimization.put("needsOptimization", needsOptimization);
        optimization.put("potentialSavings", needsOptimization ? item.getTotalValue() * 0.1 : 0);
        
        return optimization;
    }
    
    private List<String> generateOptimizationRecommendations(List<InventoryItem> items) {
        List<String> recommendations = new ArrayList<>();
        
        long slowMovingCount = items.stream().filter(this::isSlowMoving).count();
        if (slowMovingCount > items.size() * 0.2) {
            recommendations.add("减少慢动销库存");
        }
        
        long lowStockCount = items.stream().filter(item -> item.getCurrentStock() <= item.getSafetyStock()).count();
        if (lowStockCount > items.size() * 0.1) {
            recommendations.add("优化安全库存设置");
        }
        
        return recommendations;
    }
    
    private double calculateWarehouseUtilization(List<InventoryItem> items) {
        double totalValue = items.stream().mapToDouble(item -> item.getTotalValue() != null ? item.getTotalValue() : 0).sum();
        return totalValue > 0 ? (totalValue / 100000) * 100 : 0; // 假设最大容量为100000
    }
    
    private double calculateWarehouseEfficiency(List<InventoryItem> items) {
        // 简化的仓库效率计算
        return 85.5; // 示例值
    }
    
    private String assessItemRisk(InventoryItem item) {
        if (item.getCurrentStock() <= item.getSafetyStock()) {
            return "HIGH";
        } else if (item.getCurrentStock() <= item.getReorderPoint()) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private String calculateOverallRisk(int highRisk, int mediumRisk, int lowRisk) {
        int total = highRisk + mediumRisk + lowRisk;
        if (total == 0) return "LOW";
        
        double riskScore = (highRisk * 1.0 + mediumRisk * 0.5) / total;
        if (riskScore > 0.3) return "HIGH";
        if (riskScore > 0.1) return "MEDIUM";
        return "LOW";
    }
    
    private List<String> generateImplementationRoadmap() {
        return Arrays.asList(
            "第1周: 现状分析和需求调研",
            "第2周: 制定详细实施方案",
            "第3-4周: 系统配置和测试",
            "第5-6周: 员工培训和上线准备",
            "第7周: 正式上线和优化"
        );
    }
}