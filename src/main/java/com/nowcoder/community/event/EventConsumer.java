package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/8
 **/
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    /**
     * 生产者封装了Event，消费者消费Event，我们将Event转换为message,最终显示到页面。
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT , TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息为空");
            return ;
        }
        Event event = JSONObject.parseObject(record.value().toString() , Event.class);
        if(event == null){
            logger.error("消息格式错误");
            return ;
        }
        //发送通知
        Message message = new Message();
        //基础数据
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //Message内容
        Map<String , Object> content = new HashMap<>();
        content.put("userId" , event.getUserId());
        content.put("entityType" , event.getEntityType());
        content.put("entityId" , event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey() , entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
        return;
    }
}
