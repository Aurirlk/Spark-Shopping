package com.shopping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.entity.Order;
import com.shopping.entity.Product;
import com.shopping.entity.User;
import com.shopping.mapper.OrderMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.mapper.UserMapper;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据大屏 - 统计API
 */
@RestController
@RequestMapping("/admin/stats")
public class AdminStatsController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 核心指标
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        Map<String, Object> data = new HashMap<>();

        // 总订单数
        long orderCount = orderMapper.selectCount(null);
        data.put("totalOrders", orderCount);

        // 总销售额
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 3)
                        .or()
                        .eq(Order::getStatus, 2)
        );
        BigDecimal totalRevenue = allOrders.stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalRevenue", totalRevenue);

        // 用户总数
        long userCount = userMapper.selectCount(null);
        data.put("totalUsers", userCount);

        // 商品总数
        long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1)
        );
        data.put("totalProducts", productCount);

        // 本月新增订单
        long monthOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay())
        );
        data.put("monthOrders", monthOrders);

        return Result.success(data);
    }

    /**
     * 月度销售趋势
     */
    @GetMapping("/monthly-trend")
    public Result<List<Map<String, Object>>> getMonthlyTrend() {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getStatus, 1)
                        .isNotNull(Order::getCreateTime)
        );

        // 按月份分组
        Map<String, List<Order>> byMonth = orders.stream()
                .filter(o -> o.getCreateTime() != null)
                .collect(Collectors.groupingBy(o -> o.getCreateTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")
                )));

        List<Map<String, Object>> trend = new ArrayList<>();
        byMonth.forEach((month, monthOrders) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("orderCount", monthOrders.size());
            item.put("revenue", monthOrders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            trend.add(item);
        });

        trend.sort(Comparator.comparing(m -> (String) m.get("month")));
        return Result.success(trend);
    }

    /**
     * 品类销售排行
     */
    @GetMapping("/category-ranking")
    public Result<List<Map<String, Object>>> getCategoryRanking() {
        // 简化实现：返回商品按分类统计
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1)
        );

        Map<Long, Long> salesByCategory = products.stream()
                .filter(p -> p.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        Product::getCategoryId,
                        Collectors.summingLong(p -> p.getSales() != null ? p.getSales() : 0)
                ));

        // 分类名称映射
        Map<Long, String> catNames = Map.of(
                6L, "手机数码", 7L, "耳机", 8L, "笔记本",
                9L, "台式机", 10L, "冰箱", 11L, "洗衣机"
        );

        List<Map<String, Object>> ranking = salesByCategory.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", catNames.getOrDefault(e.getKey(), "其他"));
                    item.put("sales", e.getValue());
                    return item;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("sales"), (Long) a.get("sales")))
                .collect(Collectors.toList());

        return Result.success(ranking);
    }
}
