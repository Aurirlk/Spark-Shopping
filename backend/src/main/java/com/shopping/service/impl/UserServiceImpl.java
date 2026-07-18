package com.shopping.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.dto.UserLoginDTO;
import com.shopping.dto.UserRegisterDTO;
import com.shopping.entity.User;
import com.shopping.exception.BusinessException;
import com.shopping.mapper.UserMapper;
import com.shopping.service.UserService;
import com.shopping.utils.PasswordUtil;
import com.shopping.utils.UserContext;
import com.shopping.vo.UserLoginVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void register(UserRegisterDTO dto) {
        // 1. 验证密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次密码不一致");
        }

        // 2. 验证用户名是否已存在
        User existingUser = getUserByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 创建用户对象
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getUsername());
        user.setStatus(1);

        // 4. 保存用户
        save(user);
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        // 1. 查询用户
        User user = getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成Token（SaToken）
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 5. 构建返回对象
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setToken(token);

        return vo;
    }

    @Override
    public User getUserByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    @Override
    public void updateUserInfo(User user) {
        Long userId = UserContext.getUserId();
        user.setId(userId);
        updateById(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        User user = getById(userId);

        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }
}
