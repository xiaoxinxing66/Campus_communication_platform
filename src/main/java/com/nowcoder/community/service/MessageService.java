package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/3
 **/
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> selectConversations(int userId , int offset , int limit){
        return messageMapper.selectConversations(userId , offset , limit);
    }
    public int selectConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    public List<Message> selectLetters(String conversationId , int offset , int limit){
        return messageMapper.selectLetters(conversationId , offset , limit);
    }
    public int selectLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int selectLetterUnreadCount(int userId , String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
    public int addMessage(Message message){
        //过滤敏感词[标签 + 敏感词]
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids , 1);
    }

    public Message findLatestNotice(int userId , String topic){
        return messageMapper.selectLatestNotice(userId , topic);
    }
    public int findNoticeCount(int userId , String topic){
        return messageMapper.selectNoticeCount(userId , topic);
    }
    public int findNoticeUnreadCount(int userId , String topic){
        return messageMapper.selectNoticeUnreadCount(userId , topic);
    }
    public List<Message> findNotices(int userId , String topic , int offset  ,int limit){
        return messageMapper.selectNotices(userId , topic , offset , limit);
    }

}
