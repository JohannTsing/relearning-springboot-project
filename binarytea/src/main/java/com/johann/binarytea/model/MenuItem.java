package com.johann.binarytea.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 菜单选项
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Builder
@Getter
@Setter
@ToString
public class MenuItem {

    private Long id;
    private String name;
    private String size;
    private BigDecimal price;
    private Date createTime;
    private Date updateTime;



}
