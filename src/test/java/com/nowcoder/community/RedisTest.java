package com.nowcoder.community;

import com.nowcoder.community.config.RedisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/4
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testRedis(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey , 1);

        System.out.println( redisTemplate.opsForValue().get(redisKey));
    }
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "text:tx";
                //启用事务
                operations.multi();

                operations.opsForSet().add(redisKey , "123");
                operations.opsForSet().add(redisKey , "456");
                operations.opsForSet().add(redisKey , "789");
                operations.opsForSet().add(redisKey , "012");
                //Redis事务过程中进行查询是没有效果的。
                System.out.println(operations.opsForSet().members(redisKey));
                //提交事务
                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
