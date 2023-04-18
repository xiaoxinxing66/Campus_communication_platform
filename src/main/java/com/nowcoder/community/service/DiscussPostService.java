package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/18
 **/
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     *
     * @param userId
     * @param offset 起始页数
     * @param limit 每页显示条数
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
