package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.UserAddress;
import com.shopping.exception.BusinessException;
import com.shopping.mapper.UserAddressMapper;
import com.shopping.service.UserAddressService;
import com.shopping.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getUserAddresses() {
        Long userId = UserContext.getUserId();
        return list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getUpdateTime));
    }

    @Override
    public void addAddress(UserAddress address) {
        Long userId = UserContext.getUserId();
        address.setUserId(userId);

        // 如果是第一个地址，设置为默认
        long count = count(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId));
        if (count == 0) {
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }

        save(address);
    }

    @Override
    public void updateAddress(UserAddress address) {
        Long userId = UserContext.getUserId();
        address.setUserId(userId);
        updateById(address);
    }

    @Override
    public void deleteAddress(Long id) {
        Long userId = UserContext.getUserId();
        UserAddress address = getById(id);

        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 如果删除的是默认地址，将第一个地址设为默认
        if (address.getIsDefault() == 1) {
            List<UserAddress> addresses = getUserAddresses();
            if (addresses.size() > 1) {
                UserAddress firstAddress = addresses.stream()
                        .filter(a -> !a.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (firstAddress != null) {
                    firstAddress.setIsDefault(1);
                    updateById(firstAddress);
                }
            }
        }

        removeById(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id) {
        Long userId = UserContext.getUserId();

        // 取消原来的默认地址
        UserAddress oldDefault = getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1));
        if (oldDefault != null) {
            oldDefault.setIsDefault(0);
            updateById(oldDefault);
        }

        // 设置新的默认地址
        UserAddress newDefault = getById(id);
        if (newDefault == null || !newDefault.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        newDefault.setIsDefault(1);
        updateById(newDefault);
    }
}
