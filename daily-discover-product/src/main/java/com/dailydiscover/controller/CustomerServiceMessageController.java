package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.CustomerServiceMessage;
import com.dailydiscover.service.CustomerServiceMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-service-messages")
@RequiredArgsConstructor
public class CustomerServiceMessageController {

    private final CustomerServiceMessageService customerServiceMessageService;

    @GetMapping
    @ApiLog("获取所有客服消息")
    public ResponseEntity<List<CustomerServiceMessage>> getAllCustomerServiceMessages() {
        try {
            List<CustomerServiceMessage> messages = customerServiceMessageService.list();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取客服消息")
    public ResponseEntity<CustomerServiceMessage> getCustomerServiceMessageById(@PathVariable Long id) {
        try {
            CustomerServiceMessage message = customerServiceMessageService.getById(id);
            return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{conversationId}")
    @ApiLog("根据会话ID获取消息列表")
    public ResponseEntity<List<CustomerServiceMessage>> getCustomerServiceMessagesByConversationId(@PathVariable Long conversationId) {
        try {
            List<CustomerServiceMessage> messages = customerServiceMessageService.findByConversationId(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{conversationId}/unread")
    @ApiLog("查询未读消息")
    public ResponseEntity<List<CustomerServiceMessage>> getUnreadCustomerServiceMessages(@PathVariable Long conversationId) {
        try {
            List<CustomerServiceMessage> messages = customerServiceMessageService.findUnreadMessages(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{conversationId}/files")
    @ApiLog("查询包含文件的客服消息")
    public ResponseEntity<List<CustomerServiceMessage>> getCustomerServiceMessagesWithFiles(@PathVariable Long conversationId) {
        try {
            List<CustomerServiceMessage> messages = customerServiceMessageService.findMessagesWithFiles(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{conversationId}/latest")
    @ApiLog("获取会话中的最新消息")
    public ResponseEntity<CustomerServiceMessage> getLatestCustomerServiceMessage(@PathVariable Long conversationId) {
        try {
            CustomerServiceMessage message = customerServiceMessageService.getLatestMessage(conversationId);
            return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{conversationId}/count")
    @ApiLog("统计会话中的消息数量")
    public ResponseEntity<Integer> countCustomerServiceMessagesByConversationId(@PathVariable Long conversationId) {
        try {
            Integer count = customerServiceMessageService.countMessagesByConversationId(conversationId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("发送客服消息")
    public ResponseEntity<CustomerServiceMessage> sendCustomerServiceMessage(@RequestBody CustomerServiceMessage message) {
        try {
            boolean success = customerServiceMessageService.save(message);
            return success ? ResponseEntity.ok(message) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新客服消息")
    public ResponseEntity<CustomerServiceMessage> updateCustomerServiceMessage(@PathVariable Long id, @RequestBody CustomerServiceMessage message) {
        try {
            message.setId(id);
            boolean success = customerServiceMessageService.updateById(message);
            return success ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/read")
    @ApiLog("标记消息为已读")
    public ResponseEntity<Void> markCustomerServiceMessageAsRead(@PathVariable Long id) {
        try {
            boolean success = customerServiceMessageService.markMessageAsRead(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除客服消息")
    public ResponseEntity<Void> deleteCustomerServiceMessage(@PathVariable Long id) {
        try {
            boolean success = customerServiceMessageService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}