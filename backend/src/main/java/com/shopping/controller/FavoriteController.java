package com.shopping.controller;

import com.shopping.service.FavoriteService;
import com.shopping.utils.Result;
import com.shopping.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle/{productId}")
    public Result<Void> toggle(@PathVariable Long productId) {
        favoriteService.toggle(UserContext.getUserId(), productId);
        return Result.success();
    }

    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@PathVariable Long productId) {
        return Result.success(favoriteService.isFavorited(UserContext.getUserId(), productId));
    }

    @GetMapping("/list")
    public Result<List<Long>> getList() {
        return Result.success(favoriteService.getUserFavoriteIds(UserContext.getUserId()));
    }
}
