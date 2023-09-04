package com.johann.binaryteamybatis.mapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.johann.binaryteamybatis.model.MenuItem;
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
@SpringBootTest
public class MenuItemMapperTest {

    @Autowired
    private MenuItemMapper menuItemMapper;

    /**
     * 测试PageHelper分页
     */
    @Test
    public void testPagination(){
        // 不分页
        List<MenuItem> list = menuItemMapper.findAll();
        assertEquals(list.size(), 2);

        // 使用分页
        // PageHelper.startPage(1, 1); 默认会进行 count 查询，即查出总记录数
        PageHelper.startPage(1, 1);
        List<MenuItem> pageList = menuItemMapper.findAll();
        // 当前页大小
        assertEquals(pageList.size(), 1);
        assertTrue(pageList instanceof Page);

        PageInfo<MenuItem> pageInfo = new PageInfo<>(pageList);
        // 总页数
        assertEquals(pageInfo.getPages(),2);
        // 每页大小
        assertEquals(pageInfo.getPageSize(),1);
        // 当前页码
        assertEquals(pageInfo.getPageNum(),1);
        // 下页页码
        assertEquals(pageInfo.getNextPage(),2);
        // 当前页码的数据
        assertEquals(pageInfo.getList().get(0).getId(),1);

        // 查询下一页数据
        if (pageInfo.isHasNextPage()){
            PageHelper.startPage(pageInfo.getNextPage(), pageInfo.getPageSize());
            List<MenuItem> nextPageList = menuItemMapper.findAll();
            assertEquals(nextPageList.size(), 1);
            assertTrue(nextPageList instanceof Page);
            assertEquals(nextPageList.get(0).getId(),2);
        }
    }

}
