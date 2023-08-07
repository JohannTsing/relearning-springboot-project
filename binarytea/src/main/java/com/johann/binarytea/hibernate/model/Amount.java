package com.johann.binarytea.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.Money;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @Description: Amount
 * @Auther: Johann
 * @Version: 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
// Embeddable 表示这是一个嵌入类，可以嵌入到其他实体类中。
@Embeddable
public class Amount {

    @Column(name = "amount_discount")
    private int discount;

    @Column(name = "amount_total")
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyMinorAmount",
            parameters = {@Parameter(name = "currencyCode", value = "CNY")})
    private Money totalAmount;

    @Column(name = "amount_pay")
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyMinorAmount", //PersistentMoneyAmountAndCurrency
            parameters = {@Parameter(name = "currencyCode", value = "CNY")})
    private Money payAmount;
}
