package com.sivan.community.service;

import com.sivan.community.entity.User;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/5
 **/
@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    public void follow(int userId , int entityType , int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();

                operations.opsForZSet().add(followeeKey , entityId , System.currentTimeMillis());
                operations.opsForZSet().add(followerKey , userId , System.currentTimeMillis());

                return operations.exec();
            }
        });
    }
    public void unfollow(int userId , int entityType , int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();

                operations.opsForZSet().remove(followeeKey , entityId);
                operations.opsForZSet().remove(followerKey , userId);

                return operations.exec();
            }
        });
    }
    //查询用户关注的实体数量
    public long findFolloweeCount(int userId , int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //查询实体粉丝数量
    public long findFollowerCount(int entityType , int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //查询当前用户是否关注目标
    public boolean isFollowed(int userId , int entityType , int entityId){
        //查询用户关注的实体
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey , entityId) != null;
    }
    //查询用户关注的人
    public List<Map<String , Object>> findFollowees(int userId , int offset , int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(targetIds == null){
            return null;
        }
        List<Map<String , Object>> list = new ArrayList<>();
        for(Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user" , user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime" , new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
    //查询用户的粉丝
    public List<Map<String , Object>> findfollowers(int userId , int offset , int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetIds == null){
            return null;
        }
        List<Map<String , Object>> list = new ArrayList<>();
        for(Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user" , user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime" , new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
