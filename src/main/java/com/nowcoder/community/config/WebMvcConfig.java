package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import com.nowcoder.community.controller.interceptor.MessageInterceptor;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/24
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;
    /**
     * 增加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //设置拦截器，以及拦截器忽略的请求。
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css" , "/**/*/js" , "/**/*.png" , "/**/*.jpg" , "/**/*.jpeg");
        //检查登录状态的拦截器。
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css" , "/**/*/js" , "/**/*.png" , "/**/*.jpg" , "/**/*.jpeg");
        //检查未读消息的拦截器。
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css" , "/**/*/js" , "/**/*.png" , "/**/*.jpg" , "/**/*.jpeg");

    }
}
