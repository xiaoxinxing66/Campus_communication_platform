package com.sivan.community.dao;

import com.sivan.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CommentMapper {
    /**
     * 通过Entity查询评论
     * @param entityId
     * @param entityType
     * @param offset 起始页
     * @param limit 一页显示多少数据
     * @return
     */
    List<Comment> selectCommentByEntity(int entityId , int entityType ,int offset , int limit);

    int selectCountByEntity(int entityId , int entityType);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
