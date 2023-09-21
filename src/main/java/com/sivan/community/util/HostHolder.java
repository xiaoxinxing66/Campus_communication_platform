package com.sivan.community.util;

import com.sivan.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/24
 **/

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }

}
