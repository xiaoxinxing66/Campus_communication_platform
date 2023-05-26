package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/18
 **/
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    @Value("${caffeine.posts.expire-seconds}")
    private int expireTime;

    private static final Logger Logger = LoggerFactory.getLogger(DiscussPostService.class);
    //Caffeine核心接口：Cache、LoadingCache、AsyncLoadingCache

    //帖子列表缓存:按照key缓存value
    private LoadingCache<String, List<DiscussPost>> postListCache;
    //帖子总数缓存：
    private LoadingCache<Integer , Integer> postRowCache;

    @PostConstruct
    public void init(){
        //优化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireTime , TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] split = key.split(":");
                        if(split == null || split.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.valueOf(split[0]);
                        int limit = Integer.valueOf(split[1]);

                        //可以先访问二级缓存，再访问数据库

                        Logger.debug("load post list from DB");
                        //方法的返回值，装载到本地缓存中。
                        return discussPostMapper.selectDiscussPosts(0 , offset , limit , 1);
                    }
                });
        //优化帖子总数缓存
        postRowCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireTime , TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer integer) throws Exception {
                        Logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPostRows(integer);
                    }
                });
    }

    /**
     *
     * @param userId
     * @param offset 起始页数
     * @param limit 每页显示条数
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit , int ordermod) {
        //缓存首页热门帖子，首页userId为0即可，ordermod为1表示热门帖子
        if(userId == 0 && ordermod == 1){
            return postListCache.get(offset + ":" + limit);
        }
        Logger.debug("load post list from DB");

        return discussPostMapper.selectDiscussPosts(userId, offset, limit , ordermod);
    }

    public int findDiscussPostRows(int userId) {
        if(userId == 0){
            return postRowCache.get(userId);
        }
        Logger.debug("load post list from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 插入帖子时，进行敏感词过滤，并且进行标签的去除
     * @param discussPost
     * @return
     */
    public int insertDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new RuntimeException("参数不能为空");
        }
        // 转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 通过id查询帖子
     * @param id
     * @return
     */
    public DiscussPost selectDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
    public int updateCommentCount(int id , int commentCount){
        return discussPostMapper.updateCommentCount(id , commentCount);
    }
    public int updateDiscussPostType(int id , int type){
        return discussPostMapper.updateDiscussPostType(id , type);
    }
    public int updateStatus(int id , int status){
        return discussPostMapper.updateStatus(id , status);
    }
    public int updateScore(int id , double score){
        return discussPostMapper.updateScore(id , score);
    }

}
