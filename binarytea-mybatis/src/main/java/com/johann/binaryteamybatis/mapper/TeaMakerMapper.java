package com.johann.binaryteamybatis.mapper;

import com.johann.binaryteamybatis.model.TeaMaker;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Mapper
public interface TeaMakerMapper {

    @Insert("insert into t_tea_maker(name, create_time, update_time) values(#{name}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(TeaMaker teaMaker);

    @Update("update t_tea_maker set name=#{name}, update_time=now() where id=#{id}")
    int update(TeaMaker teaMaker);

    @Select("select * from t_tea_maker where id = #{id}")
    @Results(id = "teaMakerMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "name", column = "name"),
            @Result(property = "orders", column = "id",
                    // 通过 @Many 注解来实现一对多的关联查询, 通过 select 属性指定关联查询的方法, 通过 fetchType 属性指定延迟加载
                    many = @Many(select = "com.johann.binaryteamybatis.mapper.OrderMapper.findByMakerId"
                            //,fetchType = FetchType.LAZY,resultMap = "orderMap"
                    )
            ),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    TeaMaker findById(Long id);

    @Select("select * from t_tea_maker")
    @ResultMap("teaMakerMap")
    List<TeaMaker> findAll();

    /**
     * 限制查询结果的范围
     * @param rowBounds
     * @return
     */
    @Select("select * from t_tea_maker")
    @ResultMap("teaMakerMap")
    List<TeaMaker> findAllWithRowBounds(RowBounds rowBounds);

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Select("select * from t_tea_maker")
    @ResultMap("teaMakerMap")
    List<TeaMaker> findAllWithPage(int pageNum, int pageSize);
}
