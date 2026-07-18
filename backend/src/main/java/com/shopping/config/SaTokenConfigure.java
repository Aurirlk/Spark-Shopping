package com.shopping.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    // SaToken 配置
    @Bean @Primary
    public SaTokenConfig saTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName("token");
        config.setTimeout(60 * 60 * 24 * 7); // 7天
        config.setActiveTimeout(60 * 60);    // 1小时无操作冻结
        config.setIsConcurrent(false);        // 同端互斥登录
        config.setIsShare(false);
        config.setTokenStyle("jwt");
        config.setIsLog(false);
        return config;
    }

    // JWT 模式
    @Bean
    public StpLogic stpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    // 路由拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录校验：排除公开接口
            String path = SaHolder.getRequest().getRequestPath();
            if (path.startsWith("/api/user/login") || path.startsWith("/api/user/register") ||
                path.startsWith("/api/product/") || path.startsWith("/api/category/") ||
                path.startsWith("/api/search/") || path.startsWith("/api/seckill/") ||
                path.startsWith("/api/coupon/available") ||
                path.startsWith("/api/uploads/") || path.startsWith("/api/upload/")) {
                return;
            }
            cn.dev33.satoken.stp.StpUtil.checkLogin();
        })).addPathPatterns("/api/**");
    }

    // 跨域
    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
            .addInclude("/**")
            .setBeforeAuth(r -> {
                SaHolder.getResponse()
                    .setHeader("Access-Control-Allow-Origin", "*")
                    .setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                    .setHeader("Access-Control-Allow-Headers", "*")
                    .setHeader("Access-Control-Max-Age", "3600");
            });
    }
}
