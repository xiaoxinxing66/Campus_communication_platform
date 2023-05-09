package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequire;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/5
 **/
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/follow" , method = RequestMethod.POST)
    @ResponseBody
    @LoginRequire
    public String follow(int entityType , int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId() , entityType , entityId);
        //触发关注事件
        Event event = new Event().setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0 , "已关注");
    }
    @RequestMapping(value = "/unfollow" , method = RequestMethod.POST)
    @ResponseBody
    @LoginRequire
    public String unfollow(int entityType , int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId() , entityType , entityId);
        return CommunityUtil.getJSONString(0 , "已取消关注");
    }

    /**
     * 查询用户关注的人
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(value = "/followees/{userId}" , method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId , Page page , Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user" , user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId ,ENTITY_TYPE_USER));
        List<Map<String , Object>> userList = followService.findFollowees(userId , page.getOffset() , page.getLimit());
        //判断当前登录用户是否关注了关注列表中的用户。
        if(userList != null){
            for(Map<String,Object> map : userList){
                User u = (User)map.get("user");
                map.put("hasFollowed" , hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users" , userList);
        return "/site/followee";
    }
    /**
     * 查询用户的粉丝
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(value = "/followers/{userId}" , method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId , Page page , Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user" , user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER ,userId));
        List<Map<String , Object>> userList = followService.findfollowers(userId,page.getOffset() , page.getLimit());
        //判断当前登录用户是否关注了粉丝列表中的用户。
        if(userList != null){
            for(Map<String,Object> map : userList){
                User u = (User)map.get("user");
                map.put("hasFollowed" , hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users" , userList);
        return "/site/follower";
    }
    //判断用户是否关注该指定用户。
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            return false;
        }
        return followService.isFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER , userId);
    }


}
