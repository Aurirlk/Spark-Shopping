package com.shopping.controller;

import com.shopping.entity.Cart;
import com.shopping.service.CartService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.addToCart(productId, quantity);
        return Result.success("添加成功");
    }

    @PutMapping("/update/{cartId}")
    public Result<Void> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return Result.success("删除成功");
    }

    @GetMapping("/list")
    public Result<List<Cart>> getCartList() {
        List<Cart> carts = cartService.getCartList();
        return Result.success(carts);
    }

    @PutMapping("/check/{cartId}")
    public Result<Void> checkCart(@PathVariable Long cartId, @RequestParam Integer checked) {
        cartService.checkCart(cartId, checked);
        return Result.success("操作成功");
    }

    @PutMapping("/checkAll")
    public Result<Void> checkAll(@RequestParam Integer checked) {
        cartService.checkAll(checked);
        return Result.success("操作成功");
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart() {
        cartService.clearCart();
        return Result.success("清空成功");
    }

    @GetMapping("/count")
    public Result<Integer> getCartCount() {
        int count = cartService.getCartCount();
        return Result.success(count);
    }
}
