package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/18
 **/
@Controller
@CrossOrigin
public class HomeController implements CommunityConstant {
    private static final String PAGE_INDEX = "/index";
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @RequestMapping(value = "/index" ,method = RequestMethod.GET)
    public String getIndexPage(Model model , Page page ,@RequestParam(name="orderMode" , defaultValue = "0") int orderMod){
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index/?orderMode=" + orderMod);
        //因为不能直接显示userid，要显示用户名，所以使用Map存放每一条DiscussPost对应的user名。
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit() ,orderMod);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST , post.getId());
                map.put("likeCount" , likeCount);
//                long likeStatus = likeService.findEntityLikeStatus(user.getId(),ENTITY_TYPE_POST , post.getId());
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode" , orderMod);
        return PAGE_INDEX;
    }
    @RequestMapping(value = "/error" , method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    // 拒绝访问时的提示页面
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
