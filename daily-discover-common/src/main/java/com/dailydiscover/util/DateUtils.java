package com.dailydiscover.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化日期为指定格式
     * @param date 要格式化的日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : null;
    }

    /**
     * 解析日期字符串为 Date 类型
     * @param dateStr 要解析的日期字符串
     * @return 解析后的 Date 对象，如果解析失败则返回 null
     */
    public static Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


     /**
     * 将 LocalDateTime 转换为 Date
     * @param localDateTime 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        // Method implementation
        return java.sql.Timestamp.valueOf(localDateTime);
    }
}