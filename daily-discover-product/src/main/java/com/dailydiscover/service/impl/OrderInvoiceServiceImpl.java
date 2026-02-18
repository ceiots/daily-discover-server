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
        return lambdaQuery().eq(OrderInvoice::getOrderId, orderId).one();
    }
    
    @Override
    public OrderInvoice getByInvoiceNumber(String invoiceNumber) {
        return lambdaQuery().eq(OrderInvoice::getInvoiceNumber, invoiceNumber).one();
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
        invoice.setStatus("pending");
        
        save(invoice);
        return invoice;
    }
    
    @Override
    public boolean updateInvoiceStatus(Long orderId, String status) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setStatus(status);
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public boolean issueInvoice(Long orderId, String invoiceNumber) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setStatus("issued");
            invoice.setIssueTime(new java.util.Date());
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public boolean voidInvoice(Long orderId) {
        OrderInvoice invoice = getByOrderId(orderId);
        if (invoice != null) {
            invoice.setStatus("void");
            return updateById(invoice);
        }
        return false;
    }
    
    @Override
    public List<OrderInvoice> getPendingInvoices() {
        return lambdaQuery().eq(OrderInvoice::getStatus, "pending").list();
    }
    
    @Override
    public List<OrderInvoice> getIssuedInvoices() {
        return lambdaQuery().eq(OrderInvoice::getStatus, "issued").list();
    }
}