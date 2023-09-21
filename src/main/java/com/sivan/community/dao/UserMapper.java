package com.sivan.community.dao;

import com.sivan.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    /**
     *
     * @param user 传入需要插入的用户
     * @return 返回插入数据的行数
     */
    int insertUser(User user);

    /**
     *
     * @param id 传入需要修改的id
     * @param status 传入当前user状态
     * @return 返回修改的条数
     */
    int updateStatus(int id , int status);

    /**
     *  更新用户头像
     * @param id 用户id
     * @param headerUrl 用户头像url
     * @return
     */
    int updateHeader(int id , String headerUrl);

    int updatePassword(int id , String password);
}
