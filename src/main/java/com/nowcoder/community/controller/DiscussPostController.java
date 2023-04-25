package com.nowcoder.community.controller;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/25
 **/
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add" , method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title , String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403 , "没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setStatus(0);
        post.setCreateTime(new Date());
        discussPostService.insertDiscussPost(post);
        //报错以后处理。
        return CommunityUtil.getJSONString(0 , "成功！");
    }
    @RequestMapping(value = "/detail/{id}" , method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("id") int id , Model model){
        //查询帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);
        model.addAttribute("post" , discussPost);
        //用户id处理为用户作者的信息。
        User user = userService.findUserById(discussPost.getId());
        model.addAttribute("user" , user);
        //帖子回复等相关功能【补充】
        return "/site/discuss-detail";
    }

}
