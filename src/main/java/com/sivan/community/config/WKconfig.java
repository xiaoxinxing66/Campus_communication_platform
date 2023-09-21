package com.sivan.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/19
 **/
@Configuration
public class WKconfig {
    public static final Logger logger = LoggerFactory.getLogger(WKconfig.class);
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    public void init() {
        File file = new File(wkImageStorage);
        if (!file.exists()) {
            file.mkdir();
            logger.info("创建成功，目录" + wkImageStorage);
        }
    }
}
