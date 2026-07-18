package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.Favorite;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    void toggle(Long userId, Long productId);
    boolean isFavorited(Long userId, Long productId);
    List<Long> getUserFavoriteIds(Long userId);
}
