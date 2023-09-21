package com.sivan.community.service;

import com.sivan.community.dao.CommentMapper;
import com.sivan.community.entity.Comment;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/26
 **/
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper mapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;
    public List<Comment> findCommentByEntity(int entityId , int entityType , int offset , int limit){
        return mapper.selectCommentByEntity(entityId , entityType , offset , limit);
    }
    public int findCommentCount(int entityId , int entityType){
        return mapper.selectCountByEntity(entityId, entityType);
    }

    /**
     * 添加评论，并对改方法进行事务管理
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = mapper.insertComment(comment);
        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = mapper.selectCountByEntity(comment.getEntityId(), comment.getEntityType());
            discussPostService.updateCommentCount(comment.getEntityId() , count);
        }
        return rows;
    }
    public Comment findCommentById(int id){
        return mapper.selectCommentById(id);
    }
}
