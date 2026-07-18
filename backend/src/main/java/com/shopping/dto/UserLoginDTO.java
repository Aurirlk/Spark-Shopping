package com.shopping.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
