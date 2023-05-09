package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.mail.internet.ContentType;
import java.util.*;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/3
 **/
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    //处理私信列表
    @RequestMapping("/letter/list")
    public String getLetter(Model model , Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));
        //会话列表
        List<Message> messages = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());
        //除了会话列表，还有私信数量、未读消息数、发送私信的目标id
        List<Map<String , Object>> conversations = new ArrayList<>();
        if(messages != null){
            for(Message message : messages){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation" , message);
                //每个会话中的私信数量
                map.put("letterCount" , messageService.selectLetterCount(message.getConversationId()));
                //每个会话中的未读消息数
                map.put("unreadCount" , messageService.selectLetterUnreadCount(user.getId(), message.getConversationId()));
                //获取目标id，根据当前用户获取。
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target" , userService.findUserById(targetId));

                conversations.add(map);

            }
        }
        model.addAttribute("conversations" , conversations);

        //查询用户的未读消息
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId() , null);
        model.addAttribute("letterUnreadCount" , letterUnreadCount);

        //整个系统通知的未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId() , null);
        model.addAttribute("noticeUnreadCount" , noticeUnreadCount);
        return "/site/letter";
    }
    @RequestMapping("/letter/detail/{conversationId}")
    public String getLetterDrtail(@PathVariable("conversationId") String conversationId , Model model , Page page){
        //分页信息设置
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        //设置会话中的私信数量
        page.setRows(messageService.selectLetterCount(conversationId));


        // 私信列表
        List<Message> letterList = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        //不仅要添加私信列表，还要获得发送者的id信息fromUser、目标id
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 将私信列表中未读的消息，设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }
    @RequestMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName , String content){
        User user = userService.findUserByName(toName);
        if(user == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message = new Message();
        //发送者是当前用户
        message.setFromId(hostHolder.getUser().getId());
        //接收者是表单中传入的值转为的user。
        message.setToId(user.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 获取通知列表
     * @param model
     * @return
     */

    @RequestMapping("/notice/list")
    public String getNoticeList(Model model){
        //先获取当前用户
        User user = hostHolder.getUser();
        //查询评论类通知【不仅要查询通知，还需要其他信息：content中的信息[评论类型（评论的是帖子还是回复）、评论者]、评论数量的信息、未读评论的信息】
        Message message = messageService.findLatestNotice(user.getId() , TOPIC_COMMENT);

        if(message != null){
            //使用Map聚合数据。
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message" , message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);

            //将content内容中的数据聚合到Map中。
            messageVO.put("user" , userService.findUserById((int)data.get("userId")));
            messageVO.put("entityType" , data.get("entityType"));
            messageVO.put("entityId" , data.get("entityId"));
            messageVO.put("postId" , data.get("postId"));

            //查询评论数量聚合到Map中。
            int count = messageService.findNoticeCount(user.getId() , TOPIC_COMMENT);
            messageVO.put("count" , count);

            //查询未读评论数量聚合到Map
            int unread = messageService.findNoticeUnreadCount(user.getId() , TOPIC_COMMENT);
            messageVO.put("unread" , unread);
            model.addAttribute("commentNotice" , messageVO);

        }


        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId() , TOPIC_LIKE);

        if(message != null){
            //使用Map聚合数据。
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message" , message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user" , userService.findUserById((int)data.get("userId")));
            messageVO.put("entityType" , data.get("entityType"));
            messageVO.put("entityId" , data.get("entityId"));
            messageVO.put("postId" , data.get("postId"));

            int count = messageService.findNoticeCount(user.getId() , TOPIC_LIKE);
            messageVO.put("count" , count);

            int unread = messageService.findNoticeUnreadCount(user.getId() , TOPIC_LIKE);
            messageVO.put("unread" , unread);
            model.addAttribute("likeNotice" , messageVO);
        }

        //查询关注类通知
        message = messageService.findLatestNotice(user.getId() , TOPIC_FOLLOW);
        if(message != null){
            //使用Map聚合数据。
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message" , message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user" , userService.findUserById((int)data.get("userId")));
            messageVO.put("entityType" , data.get("entityType"));
            messageVO.put("entityId" , data.get("entityId"));
//            messageVO.put("postId" , data.get("postId"));

            int count = messageService.findNoticeCount(user.getId() , TOPIC_FOLLOW);
            messageVO.put("count" , count);

            int unread = messageService.findNoticeUnreadCount(user.getId() , TOPIC_FOLLOW);
            messageVO.put("unread" , unread);
            model.addAttribute("followNotice" , messageVO);
        }

        //整个系统通知的未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId() , null);
        model.addAttribute("noticeUnreadCount" , noticeUnreadCount);
        //私信未读数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId() , null);
        model.addAttribute("letterUnreadCount" , letterUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(value = "/notice/detail/{topic}" , method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic , Page page , Model model){
        User user = hostHolder.getUser();
        //设置分页
        page.setLimit(5);
        page.setPath("/notice/detail/"  + topic);
        page.setRows(messageService.findNoticeCount(user.getId() , topic));

        List<Message> noticeList = messageService.findNotices(user.getId() , topic , page.getOffset() , page.getLimit());

        List<Map<String , Object>> noticeVoList = new ArrayList<>();
        if(noticeList!= null){
            for(Message notice : noticeList){
                Map<String , Object> map = new HashMap<>();
                //存入通知
                map.put("notice" , notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String , Object> data = JSONObject.parseObject(content);
                map.put("user" , userService.findUserById((int)data.get("userId")));
                map.put("entityType" , data.get("entityType"));
                map.put("entityId" , data.get("entityId"));
                map.put("postId" , data.get("postId"));
                //通知的作者【通过id查询，都是1】
                map.put("fromUser" , userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
            model.addAttribute("notices" , noticeVoList);

            //设置已读
            List<Integer> ids = getLetterIds(noticeList);
            if (!ids.isEmpty()) {
                messageService.readMessage(ids);
            }

        }
        return "/site/notice-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                //判断接收者的身份，只有当前为接收者，才设置已读。
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
