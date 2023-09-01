package com.johann.binaryteamybatis.model;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private TeaMaker maker;
    private List<MenuItem> items;
    private Amount amount;
    private OrderStatus status;
    private Date createTime;
    private Date updateTime;
}
