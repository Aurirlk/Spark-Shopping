package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    /**
     * 添加购物车
     */
    void addToCart(Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long cartId, Integer quantity);

    /**
     * 删除购物车商品
     */
    void removeFromCart(Long cartId);

    /**
     * 获取购物车列表
     */
    List<Cart> getCartList();

    /**
     * 选中/取消选中购物车商品
     */
    void checkCart(Long cartId, Integer checked);

    /**
     * 全选/取消全选
     */
    void checkAll(Integer checked);

    /**
     * 清空购物车
     */
    void clearCart();

    /**
     * 获取购物车商品总数
     */
    int getCartCount();
}
