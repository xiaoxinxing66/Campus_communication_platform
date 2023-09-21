package com.sivan.community.controller;

import com.sivan.community.entity.*;
import com.sivan.community.event.EventProducer;
import com.sivan.community.service.CommentService;
import com.sivan.community.service.DiscussPostService;
import com.sivan.community.service.LikeService;
import com.sivan.community.service.UserService;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.CommunityUtil;
import com.sivan.community.util.HostHolder;
import com.sivan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/25
 **/
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

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

        //发帖处理,触发事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScore();
        redisTemplate.opsForSet().add(redisKey , post.getId());

        //报错以后处理。
        return CommunityUtil.getJSONString(0 , "成功！");
    }
    @RequestMapping(value = "/detail/{id}" , method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("id") int id , Model model , Page page){
        //查询帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);
        model.addAttribute("post" , discussPost);
        //用户id处理为用户作者的信息。
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user" , user);
        //帖子点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST , id);
        model.addAttribute("likeCount" , likeCount);
        //点赞状态
        int likeStatus = hostHolder.getUser()== null ? 0:
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus" , likeStatus);
        //帖子回复等相关功能【补充】
        //每页显示评论数量
        page.setLimit(5);
        //设置访问路径
        page.setPath("/discuss/detail/" + discussPost.getId());
        //一共有多少评论数据
        page.setRows(discussPost.getCommentCount());
        // 评论目标类别为1，表示是帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(
                discussPost.getId(), ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        //评论的Vo列表
        List<Map<String , Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                Map<String , Object> commentVo = new HashMap<>();
                commentVo.put("comment" , comment);
                commentVo.put("user" , userService.findUserById(comment.getUserId()));
                //评论点赞
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT , comment.getId());
                commentVo.put("likeCount" , likeCount);
                //点赞状态
                likeStatus = hostHolder.getUser()== null ? 0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus" , likeStatus);
                //查询回复
                List<Comment> replyList = commentService.findCommentByEntity(
                        comment.getId(), ENTITY_TYPE_COMMENT, 0, Integer.MAX_VALUE);
                //回复的Vo列表
                List<Map<String , Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String , Object> replyVo = new HashMap<>();
                        replyVo.put("reply" ,reply);
                        replyVo.put("user" , userService.findUserById(reply.getUserId()));
                        // 判断是否有回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        //回复目标
                        replyVo.put("target" , target);

                        //回复点赞
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT , reply.getId());
                        replyVo.put("likeCount" , likeCount);
                        //点赞状态
                        likeStatus = hostHolder.getUser()== null ? 0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus" , likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                //将回复放到comment中。表示评论的所有回复。
                commentVo.put("replys" , replyVoList);

                //  当前评论中存在的回复的数量
                int replyCount = commentService.findCommentCount(comment.getId(), ENTITY_TYPE_COMMENT);
                commentVo.put("replyCount" , replyCount);
                commentVoList.add(commentVo);
            }
        }
       model.addAttribute("comments" , commentVoList);

        return "/site/discuss-detail";
    }
    //置顶请求
    @RequestMapping(value = "/top" , method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateDiscussPostType(id , 1);

        //发帖处理,触发事件。在事件中我们同步到了es。
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
    //加精请求
    @RequestMapping(value = "/wonderful" , method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id , 1);
        //发帖处理,触发事件。在事件中我们同步到了es。
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        String redisKey = RedisKeyUtil.getPostScore();
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJSONString(0);
    }
    //拉黑请求
    @RequestMapping(value = "/delete" , method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id , 2);
        //触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }


}
