package com.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品（带分类名称）
     */
    IPage<Product> selectProductPage(Page<Product> page,
                                      @Param("categoryId") Long categoryId,
                                      @Param("keyword") String keyword,
                                      @Param("status") Integer status);
}
