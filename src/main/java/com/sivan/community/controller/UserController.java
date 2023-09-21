package com.sivan.community.controller;

import com.sivan.community.annotation.LoginRequire;
import com.sivan.community.entity.User;
import com.sivan.community.service.FollowService;
import com.sivan.community.service.LikeService;
import com.sivan.community.service.UserService;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/24
 **/
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @LoginRequire
    @RequestMapping("/setting")
    public String getSetting(){
        return "/site/setting";
    }

    /**
     * 通过表单提交的数据，直接使用参数入参。
     * @param users
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @LoginRequire
    @RequestMapping("/uploadpassword")
    public String uploadPassword(User users , String newPassword , String confirmPassword){
        System.out.println(newPassword + "--" + confirmPassword);
        User user = hostHolder.getUser();
        userService.uploadPassword(user.getId() , users.getPassword());
        return "redirect:/index";
    }
    @RequestMapping(value = "/profile/{userId}" , method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId ,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            //这样处理会直接跳转到500页面
//            throw new RuntimeException("用户不存在");

        }
        model.addAttribute("user" , user);
        //点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount" , likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount" , followeeCount);
        //粉丝数量【entityId——>userId】
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount" , followerCount);
        //登录用户是否关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.isFollowed(hostHolder.getUser().getId() , ENTITY_TYPE_USER , userId);
        }
        model.addAttribute("hasFollowed" , hasFollowed);
        return "/site/profile";
    }
}
