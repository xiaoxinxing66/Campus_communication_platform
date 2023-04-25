package com.nowcoder.community.util;

import org.springframework.http.HttpRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/24
 **/
public class CookieUtil {
    public static String getValue(HttpServletRequest request , String name){
        if(request == null || name == null){
            throw new RuntimeException("参数为空！");
        }
        for (Cookie cookie : request.getCookies()) {
            if(cookie != null && cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
