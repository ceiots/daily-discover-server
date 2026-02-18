package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.CustomerServiceAgent;
import com.dailydiscover.service.CustomerServiceAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-service-agents")
@RequiredArgsConstructor
public class CustomerServiceAgentController {

    private final CustomerServiceAgentService customerServiceAgentService;

    @GetMapping
    @ApiLog("获取所有客服人员")
    public ResponseEntity<List<CustomerServiceAgent>> getAllCustomerServiceAgents() {
        try {
            List<CustomerServiceAgent> agents = customerServiceAgentService.list();
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取客服人员")
    public ResponseEntity<CustomerServiceAgent> getCustomerServiceAgentById(@PathVariable Long id) {
        try {
            CustomerServiceAgent agent = customerServiceAgentService.getById(id);
            return agent != null ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/online")
    @ApiLog("获取在线客服人员")
    public ResponseEntity<List<CustomerServiceAgent>> getOnlineAgents() {
        try {
            List<CustomerServiceAgent> agents = customerServiceAgentService.getOnlineAgents();
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available")
    @ApiLog("获取可用客服人员")
    public ResponseEntity<List<CustomerServiceAgent>> getAvailableAgents() {
        try {
            List<CustomerServiceAgent> agents = customerServiceAgentService.getAvailableAgents();
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建客服人员")
    public ResponseEntity<CustomerServiceAgent> createCustomerServiceAgent(@RequestBody CustomerServiceAgent agent) {
        try {
            boolean success = customerServiceAgentService.save(agent);
            return success ? ResponseEntity.ok(agent) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新客服人员")
    public ResponseEntity<CustomerServiceAgent> updateCustomerServiceAgent(@PathVariable Long id, @RequestBody CustomerServiceAgent agent) {
        try {
            agent.setId(id);
            boolean success = customerServiceAgentService.updateById(agent);
            return success ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @ApiLog("更新客服人员状态")
    public ResponseEntity<Void> updateAgentStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = customerServiceAgentService.updateAgentStatus(id, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除客服人员")
    public ResponseEntity<Void> deleteCustomerServiceAgent(@PathVariable Long id) {
        try {
            boolean success = customerServiceAgentService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}