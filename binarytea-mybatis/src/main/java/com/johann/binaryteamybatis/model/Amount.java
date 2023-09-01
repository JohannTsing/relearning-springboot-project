package com.johann.binaryteamybatis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Amount {
    private int discount;
    private Money totalAmount;
    private Money payAmount;
}
