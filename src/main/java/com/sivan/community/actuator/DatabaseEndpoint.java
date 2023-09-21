package com.sivan.community.actuator;

import com.sivan.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/21
 **/
@Component
//给端点起一个id
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;
    //表示通过get请求访问方法
    @ReadOperation
    public String checkConnection(){
        try {
            Connection connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("获取连接失败");
            return CommunityUtil.getJSONString(1 ,"获取连接失败");
        }
        return CommunityUtil.getJSONString( 0 , "获取连接成功");
    }

}
