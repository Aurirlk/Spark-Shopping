package com.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 分页查询用户订单
     */
    IPage<Order> selectOrderPage(Page<Order> page, @Param("userId") Long userId, @Param("status") Integer status);
}
