package com.johann.binarytea.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;

import javax.persistence.*;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_menu")
public class MenuItem {

    @Id
    @GeneratedValue
    // 除了自增主键，还可以用 @SequenceGenerator 和 @TaleGenerator 来指定基于序列和表生成主键
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    //@SequenceGenerator(name = "sequence-generator", sequenceName = "seq menu")
    private Long id;

    private String name;

    //private Size size;

    private Money price;

    private Date createTime;

    private Date updateTime;
}
