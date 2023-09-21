package com.sivan.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
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
    @Test
    public void testHyper(){
        String redisKey = "test:hll:01";
        for(int i = 0 ; i <= 10000 ; i ++){
            redisTemplate.opsForHyperLogLog().add(redisKey , i);
        }
        for(int i = 0 ; i <= 10000 ; i ++){
            int random = (int) (Math.random() * 10000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey , random);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    @Test
    public void testHyperUnion(){
        String redisKey = "test:hll:02";
        //第一组数据
        for(int i = 0 ; i <= 10000 ; i ++){
            redisTemplate.opsForHyperLogLog().add(redisKey , i);
        }
        //第二组数据
        String redisKey1 = "test:hll:03";
        for(int i = 5001 ; i <= 15000 ; i ++){
            redisTemplate.opsForHyperLogLog().add(redisKey1 , i);
        }
        //第三组数据
        String redisKey2 = "test:hll:04";
        for(int i = 5001 ; i <= 20000 ; i ++){
            redisTemplate.opsForHyperLogLog().add(redisKey2 , i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey , redisKey , redisKey1 , redisKey2);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
    }

    //bitmap demo
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        //记录bitmap
        redisTemplate.opsForValue().setBit(redisKey , 1,true);
        redisTemplate.opsForValue().setBit(redisKey , 4,true);
        redisTemplate.opsForValue().setBit(redisKey , 7,true);
        redisTemplate.opsForValue().setBit(redisKey , 9,true);

        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
        //统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }
}
