package com.shopping.controller;

import com.shopping.entity.UserAddress;
import com.shopping.service.UserAddressService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list() {
        List<UserAddress> addresses = userAddressService.getUserAddresses();
        return Result.success(addresses);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserAddress address) {
        userAddressService.addAddress(address);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody UserAddress address) {
        userAddressService.updateAddress(address);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return Result.success("删除成功");
    }

    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Long id) {
        userAddressService.setDefaultAddress(id);
        return Result.success("设置成功");
    }
}
