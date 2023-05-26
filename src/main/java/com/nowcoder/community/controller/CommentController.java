package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/26
 **/
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping(value = "/add/{discussPostId}" ,method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int id , Comment comment , HttpServletResponse response) throws IOException {
        if("".equals(comment.getContent())){
//            return "redirect:/discuss/detail/" + id;
            throw new RuntimeException("不能发布空文本！");
        }
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件的系统通知
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId" , id);

        //评论的目标是帖子
        if(comment.getEntityType() == ENTITY_TYPE_POST ){
            DiscussPost discussPost = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());

        //评论的目标是评论
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        //发布消息
        eventProducer.fireEvent(event);

        //当前评论是评论给帖子时，触发发帖事件。
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(id);
            eventProducer.fireEvent(event);
            String redisKey = RedisKeyUtil.getPostScore();
            redisTemplate.opsForSet().add(redisKey, id);
        }



        return "redirect:/discuss/detail/" + id;
    }
}
