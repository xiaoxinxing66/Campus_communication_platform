package com.nowcoder.community.Quartz;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElaticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/19
 **/
public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化失败");
        }
    }
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElaticsearchService elaticsearchService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScore();

        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            logger.info("任务取消，没有帖子");
            return;
        }
        logger.info("任务开始，刷新帖子分数，当前有"+  operations.size() + "个帖子");
        while (operations.size() > 0){
            this.refresh((Integer) operations.pop());
        }
        logger.info("任务结束 分数刷新完毕");
        redisTemplate.delete(redisKey);
    }

    private void refresh(int postId) {
        DiscussPost discussPost = discussPostService.selectDiscussPostById(postId);
        if(discussPost == null){
            logger.error("帖子不存在" + postId);
            return;
        }
        //是否加精
        boolean wonderful = discussPost.getStatus() == 1;
        //评论数
        int commentCount = discussPost.getCommentCount();
        //点赞数量
        long likeCount =  likeService.findEntityLikeCount(ENTITY_TYPE_POST , postId);

        //权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数 = 权重  +天数
        double score = Math.log10(Math.max(w , 1)) +
                (discussPost.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600  *24);
        //更新帖子分数
        discussPostService.updateScore(postId , score);

        // 同步es数据
        discussPost.setScore(score);
        elaticsearchService.saveDiscussPost(discussPost);
    }
}
