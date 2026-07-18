package com.shopping.controller;

import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/seckill")
    public Result<List<Map<String, Object>>> getSeckillList() {
        return Result.success(jdbc.queryForList(
            "SELECT s.*,p.name as product_name FROM seckill_product s LEFT JOIN product p ON s.product_id=p.id ORDER BY s.create_time DESC"));
    }

    @PostMapping("/seckill")
    public Result<String> saveSeckill(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO seckill_product(product_id,seckill_price,stock,start_time,end_time) VALUES(?,?,?,?,?)",
            body.get("productId"), body.get("seckillPrice"), body.get("stock"), body.get("startTime"), body.get("endTime"));
        return Result.success("创建成功");
    }

    @DeleteMapping("/seckill/{id}")
    public Result<String> deleteSeckill(@PathVariable Long id) {
        jdbc.update("UPDATE seckill_product SET status=0 WHERE id=?", id);
        return Result.success("已关闭");
    }

    @GetMapping("/coupons")
    public Result<List<Map<String, Object>>> getCoupons() {
        return Result.success(jdbc.queryForList("SELECT * FROM coupon ORDER BY create_time DESC"));
    }

    @PostMapping("/coupons")
    public Result<String> saveCoupon(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO coupon(name,condition_amount,discount_amount,stock,start_time,end_time) VALUES(?,?,?,?,?,?)",
            body.get("name"), body.get("conditionAmount"), body.get("discountAmount"),
            body.get("stock"), body.get("startTime"), body.get("endTime"));
        return Result.success("创建成功");
    }
}
