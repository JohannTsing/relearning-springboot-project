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
public class TeaMaker {
    private Long id;
    private String name;
    private List<Order> orders;
    private Date createTime;
    private Date updateTime;
}
