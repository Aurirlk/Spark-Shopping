package com.shopping.vo;

import lombok.Data;

@Data
public class UserLoginVO {

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private String token;
}
