package com.johann.binaryteamybatis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;

import java.util.Date;

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
public class MenuItem {
    private Long id;
    private String name;
    private Size size;
    private Money price;
    private Date createTime;
    private Date updateTime;
}
