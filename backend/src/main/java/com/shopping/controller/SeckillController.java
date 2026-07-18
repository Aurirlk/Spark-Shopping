package com.shopping.controller;

import com.shopping.entity.SeckillProduct;
import com.shopping.utils.Result;
import com.shopping.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> list = jdbc.queryForList(
            "SELECT s.*, p.name as product_name, p.main_image, p.price as original_price " +
            "FROM seckill_product s LEFT JOIN product p ON s.product_id = p.id " +
            "WHERE s.status=1 AND s.start_time <= NOW() AND s.end_time >= NOW() " +
            "ORDER BY s.start_time");
        return Result.success(list);
    }

    @PostMapping("/buy/{seckillId}")
    public Result<String> buy(@PathVariable Long seckillId) {
        Long userId = UserContext.getUserId();
        // 检查是否已购买
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM seckill_order WHERE seckill_id=? AND user_id=?",
            Integer.class, seckillId, userId);
        if (count > 0) return Result.error("已购买过该秒杀商品");

        // 扣库存+创建订单（简化版）
        int updated = jdbc.update(
            "UPDATE seckill_product SET stock=stock-1 WHERE id=? AND stock>0", seckillId);
        if (updated == 0) return Result.error("秒杀已售罄");

        Map<String, Object> sp = jdbc.queryForMap(
            "SELECT * FROM seckill_product WHERE id=?", seckillId);
        jdbc.update("INSERT INTO seckill_order(user_id,product_id,seckill_id,quantity,price) VALUES(?,?,?,1,?)",
            userId, sp.get("product_id"), seckillId, sp.get("seckill_price"));
        return Result.success("秒杀成功！");
    }
}
