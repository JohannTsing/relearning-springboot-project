package com.johann.binaryteamybatis.support.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义一个 MoneyTypeHandler 类，这个类继承自 BaseTypeHandler 类，用于处理自定义的属性类型 Money。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
public class MoneyTypeHandler extends BaseTypeHandler<Money> {
    /**
     * 将 Money 转换为 Long 存入数据库
     * @param preparedStatement
     * @param i
     * @param money
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Money money, JdbcType jdbcType) throws SQLException {
        preparedStatement.setLong(i, money.getAmountMinorLong());
    }

    /**
     * 将数据库中的 Long 类型转换为 Money 类型
     * @param resultSet
     *          the rs
     * @param columnName
     *          Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
     * @return
     * @throws SQLException
     */
    @Override
    public Money getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return parseMoney(resultSet.getLong(columnName));
    }

    /**
     * 将数据库中的 Long 类型转换为 Money 类型
     * @param resultSet
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Money getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return parseMoney(resultSet.getLong(columnIndex));
    }

    /**
     * 将数据库中的 Long 类型转换为 Money 类型
     * @param callableStatement
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Money getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return parseMoney(callableStatement.getLong(columnIndex));
    }

    private Money parseMoney(Long value) {
        return Money.ofMinor(CurrencyUnit.of("CNY"), value);
    }
}
