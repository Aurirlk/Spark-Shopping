package com.shopping.controller;

import com.shopping.dto.UserLoginDTO;
import com.shopping.dto.UserRegisterDTO;
import com.shopping.entity.User;
import com.shopping.service.UserService;
import com.shopping.utils.Result;
import com.shopping.utils.UserContext;
import com.shopping.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Validated UserLoginDTO dto) {
        UserLoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 隐藏密码
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        return Result.success("更新成功");
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.changePassword(oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
