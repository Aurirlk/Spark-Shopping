package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.Favorite;
import com.shopping.mapper.FavoriteMapper;
import com.shopping.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    @Transactional
    public void toggle(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> w = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        Favorite exist = getOne(w);
        if (exist != null) {
            removeById(exist.getId());
        } else {
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setProductId(productId);
            f.setCreateTime(LocalDateTime.now());
            save(f);
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        return count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId)) > 0;
    }

    @Override
    public List<Long> getUserFavoriteIds(Long userId) {
        return list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime))
                .stream().map(Favorite::getProductId).collect(Collectors.toList());
    }
}
