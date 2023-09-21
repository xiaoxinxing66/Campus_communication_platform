package com.sivan.community.service;

import com.sivan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/4
 **/
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId , int entityType , int entityId , int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean member = operations.opsForSet().isMember(entityLikeKey , userId);
                operations.multi();
                if(member){
                    operations.opsForSet().remove(entityLikeKey , userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey , userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }
    //查询实体点赞数量
    public long findEntityLikeCount(int entityType , int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return  redisTemplate.opsForSet().size(entityLikeKey);
    }
    //查询实体点赞状态【当前用户是否给当前实体点赞】
    public int findEntityLikeStatus(int userId , int entityType , int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
    //查询某个用户的获赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer i = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return i == null ? 0 : i;
    }


}
