package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior_log")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Integer behaviorType;
    private String deviceType;
    private String ipAddress;
    private String sessionId;
    private LocalDateTime createTime;
}
