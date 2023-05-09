package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询当前用户会话列表，只返回最新消息。
    List<Message> selectConversations(int userId , int offset , int limit);

    //查询当前用户会话数量
    int selectConversationCount(int userId);

    //查询某个会话包含私信列表
    List<Message> selectLetters(String conversationId , int offset , int limit);

    //查询某个会话包含私信数量。
    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(int userId , String conversationId);

    //增加一条私信
    int insertMessage(Message message);

    //更改消息状态
    int updateStatus(List<Integer> ids , int status);

    //查询某个主题下最新通知【1个】
    Message selectLatestNotice(int userId , String topic);

    //某个主题包含的通知的数量
    int selectNoticeCount(int userId , String topic);

    //查询未读的通知数量
    int selectNoticeUnreadCount(int userId , String topic);

    // 查询某个主题包含的通知列表
    List<Message> selectNotices(int userId , String topic , int offset , int limit);

}
