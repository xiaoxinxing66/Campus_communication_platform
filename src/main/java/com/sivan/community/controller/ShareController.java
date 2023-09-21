package com.sivan.community.controller;

import com.sivan.community.entity.Event;
import com.sivan.community.event.EventProducer;
import com.sivan.community.util.CommunityConstant;
import com.sivan.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/19
 **/
@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    /**
     * 分享图片功能
     * @return
     */
    @RequestMapping(path="/share" , method = RequestMethod.GET)
    @ResponseBody
    public String share(@RequestParam(name="htmlUrl") String url){
        String fileName = CommunityUtil.generateUUID();
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl" , url)
                .setData("fileName" , fileName)
                .setData("suffix" , ".png");
        eventProducer.fireEvent(event);

        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl" , domain + contextPath + "/share/image/" + fileName);
        return CommunityUtil.getJSONString(0 , null , map);
    }
    //获取长图
    @RequestMapping(value = "/share/image/{fileName}" , method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName , HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空");
        }
        response.setContentType("/image/png");
        File file = new File(wkImageStorage + "\\" + fileName + ".png");;
        try {
            OutputStream outputStream = response.getOutputStream();
            FileInputStream is = new FileInputStream(file);
            byte [] buffer = new byte[1024];
            int b = 0;
            while((b = is.read(buffer)) != -1){
                outputStream.write(buffer , 0 , b);
            }
        } catch (IOException e) {
            logger.error("获取图片失败" + e.getMessage());
        }
    }




}
