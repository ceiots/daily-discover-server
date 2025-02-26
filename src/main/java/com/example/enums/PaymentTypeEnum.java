package com.example.enums;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    ALIPAY("1", "支付宝支付"),
    WXPAY("2", "微信支付");

    private final String code;
    private final String desc;

    PaymentTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 