package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.dto.UserLoginDTO;
import com.shopping.dto.UserRegisterDTO;
import com.shopping.entity.User;
import com.shopping.vo.UserLoginVO;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO dto);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);

    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
}
