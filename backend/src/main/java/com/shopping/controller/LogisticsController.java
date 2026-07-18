package com.shopping.controller;

import com.shopping.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/logistics")
public class LogisticsController {

    @GetMapping("/track/{orderId}")
    public Result<Map<String, Object>> track(@PathVariable Long orderId) {
        // 模拟物流数据
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("company", randomCompany());
        data.put("trackingNo", "SF" + System.currentTimeMillis());
        data.put("status", randomStatus());

        List<Map<String, String>> traces = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        traces.add(Map.of("time", now.minusDays(3).format(fmt), "desc", "订单已提交"));
        traces.add(Map.of("time", now.minusDays(2).format(fmt), "desc", "商品已出库，等待快递揽收"));
        traces.add(Map.of("time", now.minusDays(2).plusHours(3).format(fmt), "desc", "快递已揽收，顺丰速运 已取件"));
        traces.add(Map.of("time", now.minusDays(1).format(fmt), "desc", "快件到达【深圳中转中心】"));
        traces.add(Map.of("time", now.minusDays(1).plusHours(5).format(fmt), "desc", "快件已发往【北京中转中心】"));
        traces.add(Map.of("time", now.minusHours(10).format(fmt), "desc", "快件到达【北京中转中心】"));
        traces.add(Map.of("time", now.minusHours(4).format(fmt), "desc", "派送中，快递员【王师傅】正在为您派送"));
        traces.add(Map.of("time", now.minusHours(1).format(fmt), "desc", "已签收，感谢使用顺丰速运"));

        data.put("traces", traces);
        data.put("currentStep", 7);
        data.put("receiver", "张三 138****8000");
        data.put("address", "北京市朝阳区建国路88号");
        return Result.success(data);
    }

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String randomCompany() {
        return new String[]{"顺丰速运", "中通快递", "圆通速递", "韵达快递", "京东快递"}[new Random().nextInt(5)];
    }
    private String randomStatus() {
        return new String[]{"配送中", "已签收", "待发货"}[new Random().nextInt(3)];
    }
}
