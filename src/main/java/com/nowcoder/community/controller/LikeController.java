package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequire;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/like" , method = RequestMethod.POST)
    @ResponseBody
//    @LoginRequire
    public String like(int entityType , int entityId , int entityUserId){
        User user = hostHolder.getUser();
        likeService.like(user.getId() , entityType , entityId , entityUserId);
        //点赞数
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //点赞状态
        long likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType , entityId);

        Map<String , Object> map = new HashMap<>();
        map.put("likeCount" , likeCount);
        map.put("likeStatus" , likeStatus);
        return CommunityUtil.getJSONString(0 , null, map);
    }
}
