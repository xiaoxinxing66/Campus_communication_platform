package com.sivan.community.controller;

import com.sivan.community.annotation.LoginRequire;
import com.sivan.community.entity.Event;
import com.sivan.community.entity.User;
import com.sivan.community.event.EventProducer;
import com.sivan.community.service.LikeService;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.CommunityUtil;
import com.sivan.community.util.HostHolder;
import com.sivan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/4
 **/
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/like" , method = RequestMethod.POST)
    @ResponseBody
    @LoginRequire
    public String like(int entityType , int entityId , int entityUserId , int postId){
        User user = hostHolder.getUser();
        likeService.like(user.getId() , entityType , entityId , entityUserId);
        //点赞数
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //点赞状态
        long likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType , entityId);

        Map<String , Object> map = new HashMap<>();
        map.put("likeCount" , likeCount);
        map.put("likeStatus" , likeStatus);

        //触发点赞事件
        if(likeStatus == 1){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    //传入的点赞的帖子Id
                    .setData("postId" ,postId);
            eventProducer.fireEvent(event);
        }
        if(entityType == ENTITY_TYPE_POST){
            String redisKey = RedisKeyUtil.getPostScore();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJSONString(0 , null, map);
    }
}
