package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取当前用户的收货地址列表
     */
    List<UserAddress> getUserAddresses();

    /**
     * 添加收货地址
     */
    void addAddress(UserAddress address);

    /**
     * 更新收货地址
     */
    void updateAddress(UserAddress address);

    /**
     * 删除收货地址
     */
    void deleteAddress(Long id);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long id);
}
