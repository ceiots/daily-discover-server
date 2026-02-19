package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.OrderInvoiceMapper;
import com.dailydiscover.model.OrderInvoice;
import com.dailydiscover.service.OrderInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderInvoiceServiceImpl extends ServiceImpl<OrderInvoiceMapper, OrderInvoice> implements OrderInvoiceService {
    
    @Autowired
    private OrderInvoiceMapper orderInvoiceMapper;
    
    @Override
    public OrderInvoice getByOrderId(Long orderId) {
        return orderInvoiceMapper.findByOrderId(orderId);
    }
    
    @Override
    public OrderInvoice getByInvoiceNumber(String invoiceNumber) {
        return orderInvoiceMapper.findByInvoiceNo(invoiceNumber);
    }
    
    @Override
    public OrderInvoice createInvoice(Long orderId, String invoiceType, String invoiceTitle, 
                                    String taxNumber, String invoiceContent) {
        OrderInvoice invoice = new OrderInvoice();
        invoice.setOrderId(orderId);
        invoice.setInvoiceType(invoiceType);
        invoice.setInvoiceTitle(invoiceTitle);
        invoice.setTaxNumber(taxNumber);
        invoice.setInvoiceContent(invoiceContent);
        invoice.setInvoiceStatus("pending");
        
        save(invoice);
        return invoice;
    }
    
    @Override
    public boolean updateInvoiceStatus(Long orderId, String status) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setInvoiceStatus(status);
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public boolean issueInvoice(Long orderId, String invoiceNumber) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceStatus("issued");
            invoice.setIssuedAt(java.time.LocalDateTime.now());
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public boolean voidInvoice(Long orderId) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setInvoiceStatus("void");
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public List<OrderInvoice> getPendingInvoices() {
        return orderInvoiceMapper.findPendingInvoices();
    }
    
    @Override
    public List<OrderInvoice> getIssuedInvoices() {
        return orderInvoiceMapper.findIssuedInvoices();
    }
}