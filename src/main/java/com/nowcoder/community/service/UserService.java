package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/18
 **/
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询到用户id时，不显示id，显示用户的名称。
     * @param id
     * @return
     */
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

}
