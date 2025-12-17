package com.dailydiscover.common.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class OrderStatusTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int status = rs.getInt(columnName);
        return getStatusText(status);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int status = rs.getInt(columnIndex);
        return getStatusText(status);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int status = cs.getInt(columnIndex);
        return getStatusText(status);
    }

    private String getStatusText(int status) {
        switch (status) {
            case 1:
                return "待付款";
            case 2:
                return "待发货";
            case 3:
                return "待收货";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}