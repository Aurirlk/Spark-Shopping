package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.Cart;
import com.shopping.entity.Product;
import com.shopping.exception.BusinessException;
import com.shopping.mapper.CartMapper;
import com.shopping.service.CartService;
import com.shopping.service.ProductService;
import com.shopping.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void addToCart(Long productId, Integer quantity) {
        Long userId = UserContext.getUserId();

        // 1. 验证商品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 验证商品是否上架
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }

        // 3. 验证库存
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 4. 检查购物车中是否已有该商品
        Cart existingCart = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));

        if (existingCart != null) {
            // 更新数量
            int newQuantity = existingCart.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            existingCart.setQuantity(newQuantity);
            updateById(existingCart);
        } else {
            // 新增购物车记录
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1); // 默认选中
            save(cart);
        }
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Long userId = UserContext.getUserId();

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        // 验证库存
        Product product = productService.getById(cart.getProductId());
        if (quantity > product.getStock()) {
            throw new BusinessException("库存不足");
        }

        if (quantity <= 0) {
            // 数量为0则删除
            removeById(cartId);
        } else {
            cart.setQuantity(quantity);
            updateById(cart);
        }
    }

    @Override
    public void removeFromCart(Long cartId) {
        Long userId = UserContext.getUserId();

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        removeById(cartId);
    }

    @Override
    public List<Cart> getCartList() {
        Long userId = UserContext.getUserId();

        List<Cart> carts = list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime));

        // 填充商品信息
        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());
            if (product != null) {
                cart.setProduct(product);
                // 计算小计
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                cart.setSubtotal(subtotal);
            }
        }

        return carts;
    }

    @Override
    public void checkCart(Long cartId, Integer checked) {
        Long userId = UserContext.getUserId();

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        cart.setChecked(checked);
        updateById(cart);
    }

    @Override
    public void checkAll(Integer checked) {
        Long userId = UserContext.getUserId();

        List<Cart> carts = list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        for (Cart cart : carts) {
            cart.setChecked(checked);
        }

        updateBatchById(carts);
    }

    @Override
    public void clearCart() {
        Long userId = UserContext.getUserId();

        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
    }

    @Override
    public int getCartCount() {
        Long userId = UserContext.getUserId();

        return (int) count(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
    }
}
