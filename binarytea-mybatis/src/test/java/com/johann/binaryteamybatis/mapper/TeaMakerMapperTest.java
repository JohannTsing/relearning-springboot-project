package com.johann.binaryteamybatis.mapper;

import com.github.pagehelper.PageInfo;
import com.johann.binaryteamybatis.model.TeaMaker;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest(properties = {"pagehelper.offset-as-page-num=true", "pagehelper.row-bounds-with-count=true"})
public class TeaMakerMapperTest {

    @Autowired
    private TeaMakerMapper teaMakerMapper;

    @Test
    public void testPagination(){
        // 通过 RowBounds 对象或者直接在方法参数中指定分页信息
//        // TODO MD,添加了“pagehelper.offset-as-page-num=true”，为什么此处还是使用的 offset 而不是 pageNum
//        List<TeaMaker> rowBoundsList = teaMakerMapper.findAllWithRowBounds(new RowBounds(1, 1));
//        assertEquals(rowBoundsList instanceof List, true);
//        PageInfo pageInfo = new PageInfo<>(rowBoundsList);
//        // 当前页大小
//        assertEquals(rowBoundsList.size(), 1);
//        // TODO MD,添加了“pagehelper.row-bounds-with-count=true”，为什么此处查到的总页数为 1 而不是 2
//        assertEquals(pageInfo.getPages(), 1);
//        // 每页大小
//        assertEquals(pageInfo.getPageSize(), 1);
//        // 当前页码
//        assertEquals(pageInfo.getPageNum(), 2);
//        // 是否有下一页
//        assertEquals(pageInfo.isHasNextPage(),false);
//        // 下页页码
//        //assertEquals(pageInfo.getNextPage(), 2);

        // 直接通过findXxxWithPage方法指定分页信息
        // TODO findAllWithPage方法中的分页信息没有生效，此时查询的是全部数据
        List<TeaMaker> pageList = teaMakerMapper.findAllWithPage(1,1);
        PageInfo pageInfo2 = new PageInfo<>(pageList);
        // 当前页大小
        assertEquals(pageList.size(), 2);
        // 总页数，此处不会进行 count 查询，所以总页数为 1
        assertEquals(pageInfo2.getPages(), 1);
        // 每页大小
        assertEquals(pageInfo2.getPageSize(), 2);
        // 当前页码
        assertEquals(pageInfo2.getPageNum(), 1);
        // 上一页页码
        assertEquals(pageInfo2.getPrePage(), 0);
        // 是否有下一页
        assertEquals(pageInfo2.isHasNextPage(),false);
    }
}
