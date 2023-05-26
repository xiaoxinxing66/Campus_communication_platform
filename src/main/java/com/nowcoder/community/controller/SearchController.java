package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElaticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/15
 **/
@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElaticsearchService elaticsearchService;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx 方式传递参数
    @RequestMapping(path="/search" , method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchPost = elaticsearchService.searchPost(keyword, page.getCurrent() - 1, page.getLimit());
        //聚合数据
        List<Map<String , Object>> discussPost = new ArrayList<>();
        if(searchPost != null){
            for(DiscussPost post : searchPost){
                Map<String, Object> map = new HashMap<>();
                //封装帖子
                map.put("post" , post);
                //封装帖子作者
                map.put("user" , userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount" , likeService.findEntityLikeCount(ENTITY_TYPE_POST , post.getId()));

                discussPost.add(map);
            }
        }
        model.addAttribute("discussPost" , discussPost);
        model.addAttribute("keyword" , keyword);
        //分页
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchPost==null ? 0 : (int) searchPost.getTotalElements());

        return "/site/search";
    }
}
