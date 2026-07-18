package com.shopping.controller;

import com.shopping.utils.Result;
import com.shopping.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/checkin")
public class CheckinController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping("/do")
    public Result<Map<String, Object>> checkin() {
        Long userId = UserContext.getUserId();
        LocalDate today = LocalDate.now();
        // 是否已签到
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM checkin WHERE user_id=? AND checkin_date=?", Integer.class, userId, today);
        if (count > 0) return Result.error("今天已签到");

        // 连续签到天数
        Integer streak = jdbc.queryForObject(
            "SELECT COUNT(*) FROM checkin WHERE user_id=? AND checkin_date >= DATE_SUB(?, INTERVAL 7 DAY)",
            Integer.class, userId, today);

        int points = Math.min(streak + 1, 7);
        jdbc.update("INSERT INTO checkin(user_id,checkin_date,points) VALUES(?,?,?)", userId, today, points);

        Map<String, Object> data = new HashMap<>();
        data.put("points", points);
        data.put("streak", streak + 1);
        data.put("message", "签到成功！获得 " + points + " 积分");
        return Result.success(data);
    }

    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        Long userId = UserContext.getUserId();
        LocalDate today = LocalDate.now();
        Integer todayChecked = jdbc.queryForObject(
            "SELECT COUNT(*) FROM checkin WHERE user_id=? AND checkin_date=?", Integer.class, userId, today);
        Integer totalPoints = jdbc.queryForObject(
            "SELECT COALESCE(SUM(points),0) FROM checkin WHERE user_id=?", Integer.class, userId);
        // 最近7天签到
        List<Map<String, Object>> week = jdbc.queryForList(
            "SELECT checkin_date,points FROM checkin WHERE user_id=? AND checkin_date >= DATE_SUB(?, INTERVAL 7 DAY) ORDER BY checkin_date",
            userId, today);
        Map<String, Object> data = new HashMap<>();
        data.put("todayChecked", todayChecked > 0);
        data.put("totalPoints", totalPoints);
        data.put("weekCheckins", week);
        return Result.success(data);
    }
}
